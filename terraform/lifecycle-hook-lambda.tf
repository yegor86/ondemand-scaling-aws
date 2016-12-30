data "archive_file" "lifecycle-hook-archive" {
    type = "zip"
    source_dir = "${path.module}/../lifecycle-hook"
    output_path = "${path.module}/../build/lifecycle-hook.zip"
}

// Create 'lifecycle hook' lambda function
resource "aws_lambda_function" "lifecycle_hook" {
    filename = "${data.archive_file.lifecycle-hook-archive.output_path}"
    function_name = "lifecycle-hook"
    role = "${aws_iam_role.generic_role.arn}"
    handler = "index.handler"
    runtime = "python2.7"
    timeout = 300
    memory_size = 256
    description = "Auto Scaling Lifecycle Hook Lambda"
    source_code_hash = "${base64sha256(file("${data.archive_file.lifecycle-hook-archive.output_path}"))}"
}

// Create SNS topic
resource "aws_sns_topic" "lifecycle_hook_topic" {
  name = "lifecycle_hook_topic"
}

// Subscribe the Lambda function to the SNS topic
resource "aws_sns_topic_subscription" "sns_target" {
    topic_arn = "${aws_sns_topic.lifecycle_hook_topic.arn}"
    protocol  = "lambda"
    endpoint  = "${aws_lambda_function.lifecycle_hook.arn}"
}

// Grant permissions on the lambda function to the SNS topic
resource "aws_lambda_permission" "allow_from_sns" {
    statement_id = "AllowInvokeFromSns"
    action = "lambda:InvokeFunction"
    function_name = "${aws_lambda_function.lifecycle_hook.arn}"
    principal = "sns.amazonaws.com"
    source_arn = "${aws_sns_topic.lifecycle_hook_topic.arn}"
}

// Put the lifecycle hook
resource "aws_autoscaling_lifecycle_hook" "as_lifecycle_hook" {
    name = "as_lifecycle_hook"
    autoscaling_group_name = "${aws_autoscaling_group.as_group.name}"
    default_result = "ABANDON"
    heartbeat_timeout = 1200
    lifecycle_transition = "autoscaling:EC2_INSTANCE_LAUNCHING"
    notification_metadata = <<EOF
{
  "foo": "bar"
}
EOF
    notification_target_arn = "${aws_sns_topic.lifecycle_hook_topic.arn}"
    role_arn = "${aws_iam_role.lifecycle_hook_iam_role.arn}"
}

// Lifecycle hook execution role
resource "aws_iam_role" "lifecycle_hook_iam_role" {
    name = "lifecycle_hook_iam_role"
    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [ {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "autoscaling.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
  } ]
}
EOF
}

// Policy to publish to our SNS topic
resource "aws_iam_role_policy" "sns_lifecycle_hook_access_policy" {
    name = "sns_lifecycle_hook_access_policy"
    role = "${aws_iam_role.lifecycle_hook_iam_role.id}"
    policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [ {
      "Effect": "Allow",
      "Resource": "${aws_sns_topic.lifecycle_hook_topic.arn}",
      "Action": [
        "sns:Publish"
      ]
  } ]
}
EOF
}
