data "archive_file" "workflow-starter-archive" {
    type = "zip"
    source_dir = "${path.module}/../monitoring"
    output_path = "${path.module}/../build/workflow-starter.zip"
}

// Create 'workflow-starter' lambda function
resource "aws_lambda_function" "workflow-starter" {
    filename = "${data.archive_file.workflow-starter-archive.output_path}"
    function_name = "workflow-starter"
    role = "${aws_iam_role.generic_role.arn}"
    handler = "swf.starter.WorkflowStarter::handler"
    runtime = "java8"
    timeout = 300
    memory_size = 256
    description = "WorkflowStarter Lambda"
    source_code_hash = "${base64sha256(file("${data.archive_file.workflow-starter-archive.output_path}"))}"
}