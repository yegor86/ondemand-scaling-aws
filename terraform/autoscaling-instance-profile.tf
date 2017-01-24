resource "aws_iam_instance_profile" "ondemand_scaling_profile" {
    name = "ondemand_scaling_profile"
    roles = ["${aws_iam_role.ondemand_scaling_role.name}"]
}

resource "aws_iam_role" "ondemand_scaling_role" {
    name = "ondemand_scaling_role"
    path = "/"
    assume_role_policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": "sts:AssumeRole",
            "Principal": {
               "Service": "ec2.amazonaws.com"
            },
            "Effect": "Allow"
        }
    ]
}
EOF
}

// Policy to access SWF
resource "aws_iam_role_policy" "swf_access_policy" {
    name = "swf_access_policy"
    role = "${aws_iam_role.ondemand_scaling_role.id}"
    policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "swf:*"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF
}

// Policy to access S3
resource "aws_iam_role_policy" "s3_access_policy" {
    name = "s3_access_policy"
    role = "${aws_iam_role.ondemand_scaling_role.id}"
    policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "s3:*",
      "Resource": "*"
    }
  ]
}
EOF
}

// Policy to access DynamoDb
resource "aws_iam_role_policy" "dynamodb_access_policy" {
    name = "dynamodb_access_policy"
    role = "${aws_iam_role.ondemand_scaling_role.id}"
    policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "dynamodb:*"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF
}
