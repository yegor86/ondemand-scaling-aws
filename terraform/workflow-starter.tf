// Create 'workflow-starter' lambda function
resource "aws_lambda_function" "workflow-starter" {
    filename = "${path.module}/../workflow-starter/target/workflow-starter-0.0.1.jar"
    function_name = "workflow-starter"
    role = "${aws_iam_role.generic_role.arn}"
    handler = "swf.starter.WorkflowStarter::handler"
    runtime = "java8"
    timeout = 300
    memory_size = 256
    description = "WorkflowStarter Lambda"
    source_code_hash = "${base64sha256(file("${path.module}/../workflow-starter/target/workflow-starter-0.0.1.jar"))}"
}