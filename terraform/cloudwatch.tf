resource "aws_autoscaling_policy" "up_policy" {
    name = "up_policy"
    scaling_adjustment = 1
    adjustment_type = "ChangeInCapacity"
    cooldown = 120
    autoscaling_group_name = "${aws_autoscaling_group.as_group.name}"
}

resource "aws_cloudwatch_metric_alarm" "high_openworkflows_alarm" {
    alarm_name = "high_open_tasks_alarm"
    comparison_operator = "GreaterThanOrEqualToThreshold"
    evaluation_periods = "1"
    metric_name = "open_tasks"
    namespace = "OnDemandScaling"
    period = "60"
    statistic = "Average"
    threshold = "1"
    alarm_description = "Scale up if open tasks exceed 1 for 0 minutes"
    alarm_actions = ["${aws_autoscaling_policy.up_policy.arn}"]
}

resource "aws_autoscaling_policy" "down_policy" {
    name = "down_policy"
    scaling_adjustment = -1
    adjustment_type = "ChangeInCapacity"
    cooldown = 300
    autoscaling_group_name = "${aws_autoscaling_group.as_group.name}"
}

resource "aws_cloudwatch_metric_alarm" "low_openworkflows_alarm" {
    alarm_name = "low_open_tasks_alarm"
    comparison_operator = "LessThanOrEqualToThreshold"
    evaluation_periods = "1"
    metric_name = "open_tasks"
    namespace = "OnDemandScaling"
    period = "60"
    statistic = "Average"
    threshold = "0"
    alarm_description = "Scale down if open tasks are less or equal to 0 for 0 minutes"
    alarm_actions = ["${aws_autoscaling_policy.down_policy.arn}"]
}