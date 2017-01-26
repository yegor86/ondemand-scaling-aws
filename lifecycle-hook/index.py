import sys
import boto3
import jmespath
from fabric.api import *
import json
import config

env.user = config.username
env.key_filename = config.private_key
env.connection_attempts = 15
env.warn_only = True
MAX_RETRY = 10

ec2 = boto3.client('ec2')
asg_client = boto3.client('autoscaling')

def handler(event, context):
    print(json.dumps(event))
    
    msg_obj = jmespath.search('Records[0].Sns.Message', event)
    message = json.loads(str(msg_obj))
    prepare_ec2(message)
    
    response = asg_client.complete_lifecycle_action(
        LifecycleHookName=message['LifecycleHookName'],
        AutoScalingGroupName=message['AutoScalingGroupName'],
        LifecycleActionToken=message['LifecycleActionToken'],
        LifecycleActionResult='CONTINUE',
        InstanceId=message['EC2InstanceId']
    )
    print('Notify AutoScaling of the instance status')
    
    
def prepare_ec2(message):
    instance_id = message['EC2InstanceId']
        
    response = ec2.create_tags(
        Resources=[
            instance_id,
        ],
        Tags=[
            {
                'Key': 'Name',
                'Value': 'ondemand-autoscaling-activity'
            }
        ]
    )    
    ip_address = get_ip(instance_id)
    env.hosts = [ip_address]
    print('EC2 instance Public IP: %s' % ip_address)
    execute(install_tools)

def get_ip(instance_id):
    instance_resp = ec2.describe_instances(
        InstanceIds=[
            instance_id
        ]
    )    
    print('describe_instances:')
    print(instance_resp)
    return jmespath.search('Reservations[0].Instances[0].PublicIpAddress', instance_resp)

def get_cmds():
    
    return [
        {'description': 'Download application', 'command': 'aws s3 cp s3://ic-ondeman-scaling/apps/agent-0.0.1-SNAPSHOT.tar.gz /home/ec2-user/agent-0.0.1-SNAPSHOT.tar.gz'},
        {'description': 'install application', 'command': 'tar -zxvf agent-0.0.1-SNAPSHOT.tar.gz -C /home/ec2-user'},
        {'description': 'install application', 'command': 'cp /home/ec2-user/activity-host.conf /etc/init/'},
        {'description': 'Launch application', 'command': 'initctl start activity-host'}
    ]
    
def install_tools():
    for item in get_cmds():
        print('%s: %s' % (item['description'], item['command']))
        retry_count = 0
        result = sudo(item['command'])        
        while retry_count < MAX_RETRY and result.failed:
            retry_count += 1
            print('Retry instruction %s...' % item['command'])
            result = sudo(item['command'])
            
if __name__ == '__main__':
    eventJson = r'''
    {
        "Records": [
            {
                "EventVersion": "1.0",
                "EventSubscriptionArn": "arn:aws:sns:us-east-1:858524945867:lifecycle_hook_topic:3e0b4460-8bba-4e65-898e-36714db7aed1",
                "EventSource": "aws:sns",
                "Sns": {
                    "SignatureVersion": "1",
                    "Timestamp": "2016-09-16T22:45:58.738Z",
                    "Signature": "H2Hxa1FmFu+vAtkcRVT5fDRtVWGMD0q86cQBSrDI0Zw6GExfhsMNUj/yKPH/7Dj9eqsDzTPzL9x7BdcJO1tPnhXC8qcAwmGKidXqFfXZRTwwx+5S96GzGVw68WFGI77Nx6gYCEXcJU7xlG6XtrKSHmltP30GOe3Gfr6SMyIcVyG3obqERM9ya4qkb1bs+5kvkHpXit5uJlQgmoWKDA/KLjV8GsTrj9pj/ep31Wdm2lpaTjfVXW/VoxsUAuz2eZHwgsGykdCemlDJVF6OI6iYKMNIDnPbh8DKbE4jwemmskQrcuGutiK2TzNkE6RBNsWFGsjnbXr+oeKrka+3DmdO+w==",
                    "SigningCertUrl": "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-b95095beb82e8f6a046b3aafc7f4149a.pem",
                    "MessageId": "8cb60bf0-fc08-5727-9797-5ba6846e9d10",
                    "Message": "{\"LifecycleHookName\":\"as_lifecycle_hook\",\"AccountId\":\"858524945867\",\"RequestId\":\"21772d78-cff2-440a-adc2-5c197d707e76\",\"LifecycleTransition\":\"autoscaling:EC2_INSTANCE_LAUNCHING\",\"AutoScalingGroupName\":\"tf-asg-20160914220637500813050vcd\",\"Service\":\"AWS Auto Scaling\",\"Time\":\"2016-09-16T22:45:58.672Z\",\"EC2InstanceId\":\"i-054b9f2d4f8c50aad\",\"NotificationMetadata\":\"{\\n  \\\"foo\\\": \\\"bar\\\"\\n}\\n\",\"LifecycleActionToken\":\"433e1222-b8f6-4362-a451-ae33fe442321\"}",
                    "MessageAttributes": {},
                    "Type": "Notification",
                    "UnsubscribeUrl": "https://sns.us-east-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-east-1:858524945867:lifecycle_hook_topic:3e0b4460-8bba-4e65-898e-36714db7aed1",
                    "TopicArn": "arn:aws:sns:us-east-1:858524945867:lifecycle_hook_topic",
                    "Subject": "Auto Scaling:  Lifecycle action 'LAUNCHING' for instance i-0c9d27b1b3f085e84 in progress."
                }
            }
        ]
    }
    '''
    event = json.loads(eventJson)
    handler(event, {})

