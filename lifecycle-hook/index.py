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
                'Value': 'ondemand-autoscaling'
            }
        ]
    )    
    ip_address = get_ip(instance_id)
    env.hosts = [ip_address]
    print('EC2 instance Public IP: %s' % ip_address)

def get_ip(instance_id):
    instance_resp = ec2.describe_instances(
        InstanceIds=[
            instance_id
        ]
    )    
    print('describe_instances:')
    print(instance_resp)
    return jmespath.search('Reservations[0].Instances[0].PublicIpAddress', instance_resp)

