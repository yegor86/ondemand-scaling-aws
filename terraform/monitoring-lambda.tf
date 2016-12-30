data "archive_file" "monitoring-archive" {
    type = "zip"
    source_dir = "${path.module}/../monitoring"
    output_path = "${path.module}/../build/monitoring.zip"
}

// Create 'monitoring' lambda function
resource "aws_lambda_function" "monitoring" {
    filename = "${data.archive_file.monitoring-archive.output_path}"
    function_name = "monitoring"
    role = "${aws_iam_role.generic_role.arn}"
    handler = "index.handler"
    runtime = "python2.7"
    timeout = 300
    memory_size = 256
    description = "Monitoring Activity Worker TaskList Lambda"
    source_code_hash = "${base64sha256(file("${data.archive_file.monitoring-archive.output_path}"))}"
}

// Configure scheduled lambda which fires every minutes
resource "aws_cloudwatch_event_rule" "every_minute" {
    name = "every_minute"
    description = "Fires every minute"
    schedule_expression = "rate(1 minute)"
}

resource "aws_cloudwatch_event_target" "monitor_metric_every_minute" {
    rule = "${aws_cloudwatch_event_rule.every_minute.name}"
    target_id = "monitoring"
    arn = "${aws_lambda_function.monitoring.arn}"
}

// Allow Execution From CloudWatch
resource "aws_lambda_permission" "allow_clowd_watch_to_call_monitoring_lambda" {
    statement_id = "AllowExecutionFromCloudWatch"
    action = "lambda:InvokeFunction"
    function_name = "${aws_lambda_function.monitoring.function_name}"
    principal = "events.amazonaws.com" 
    source_arn = "${aws_cloudwatch_event_rule.every_minute.arn}"
}