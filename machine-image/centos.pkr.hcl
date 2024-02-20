packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
    }
  }
}

variable "project_id" {
  type    = string
  default = "csye6225-dev-414521"
}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "script_paths" {
  default = [
    "../startup-scripts/install-db.sh",
    "../startup-scripts/install-java-maven-tomcat.sh"
  ]
}

source "googlecompute" "custom_image" {
  project_id       = var.project_id
  source_image     = "centos-stream-8-v20240110"
  image_name       = "centos-{{timestamp}}"
  image_description = "Custom CentOS Stream 8 image with Java and Tomcat"
  image_labels     = {
    created_by   = "packer"
    environment  = "dev"
  }
  zone             = "us-central1-a"
  ssh_username     = var.ssh_username
  use_internal_ip  = false
}

build {
  sources = ["source.googlecompute.custom_image"]

  provisioner "shell" {
    scripts = var.script_paths
  }

  provisioner "shell" {
    inline = [
      "sudo yum install -y unzip"
    ]
  }

  provisioner "file" {
    source = "../webapp.zip"
    destination = "/home/admin/webapp.zip"
  }

  provisioner "shell" {
    inline = [
      "sudo mv /tmp/webapp.zip /"
    ]
  }
}
