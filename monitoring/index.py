import boto3
from datetime import datetime

domain_name = 'ondemand.scaling'
taskListName = 'taskList'

swf_client = boto3.client('swf')
cw_client = boto3.client('cloudwatch')

def handler(event, context):
    response = swf_client.count_open_workflow_executions(
        domain=domain_name,
        startTimeFilter={
            'oldestDate': datetime(2015, 1, 1),
            'latestDate': datetime(3017, 1, 1)
        }
    )
    openTasks = response.get('count', 0)
    
    clresponse = cw_client.put_metric_data(
        Namespace = 'OnDemandScaling',
        MetricData=[
            {
                'MetricName': "open_tasks",
                'Unit': 'Count',
                'Value': openTasks
            }
        ]
    )
    
    return response
    
if __name__ == '__main__':
    handler({}, {})    