// Lambda execution role
resource "aws_iam_role" "generic_role" {
    name = "generic_role"
    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

// Policy to access resources from Lambda
resource "aws_iam_role_policy" "generic_policy" {
    name = "generic_policy"
    role = "${aws_iam_role.generic_role.id}"
    policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "lambda:DeleteFunction",
        "swf:CountPendingActivityTasks",
        "swf:CountOpenWorkflowExecutions",
        "ec2:DescribeInstances",
        "ec2:CreateNetworkInterface",
        "ec2:AttachNetworkInterface",
        "ec2:DescribeNetworkInterfaces",
        "ec2:CreateTags",
        "ec2:RebootInstances",
        "autoscaling:CompleteLifecycleAction"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF
}

// Policy to write logs to CloudWatch
resource "aws_iam_role_policy" "logs_access_policy" {
    name = "logs_access_policy"
    role = "${aws_iam_role.generic_role.id}"
    policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:*"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF
}