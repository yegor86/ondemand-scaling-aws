data "aws_ami" "redhat_ami" {
  filter {
    name = "image-id"
    values = ["${var.image_id}"]
  }
}

resource "aws_launch_configuration" "as_conf" {
    name = "as_conf"
    image_id = "${data.aws_ami.redhat_ami.id}"
    instance_type = "${var.instance_type}"
    // spot_price = "0.42"
    key_name = "${var.key_pair_name}"
    security_groups = ["${var.security_group_id}"]
    associate_public_ip_address = true
    iam_instance_profile = "ondemand_scaling_profile"
}

resource "aws_autoscaling_group" "as_group" {
  	max_size = 1
  	min_size = 0
  	health_check_type = "EC2"
  	wait_for_capacity_timeout = "0"
  	vpc_zone_identifier = ["${var.subnet_id}"]
  	launch_configuration = "${aws_launch_configuration.as_conf.name}"
}