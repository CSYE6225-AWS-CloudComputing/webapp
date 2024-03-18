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
    "./startup-scripts/install-java-maven-tomcat.sh",
    "./startup-scripts/create-no-login-user.sh",
    "./startup-scripts/log-config.sh"
  ]
}

variable "image_description" {
  default = "Custom image for springboot application"
}

variable "base_image" {
  default = "centos-stream-8-v20240110"
}

variable zone {
  default = "us-central1-a"
}

source "googlecompute" "custom_image" {
  project_id        = var.project_id
  source_image      = var.base_image
  image_name        = "centos-{{timestamp}}"
  image_description = var.image_description
  image_labels = {
    created_by  = "packer"
    environment = "dev"
  }
  zone            = var.zone
  ssh_username    = var.ssh_username
  use_internal_ip = false
}

build {
  sources = ["source.googlecompute.custom_image"]

  provisioner "file" {
    source      = "./webapp.zip"
    destination = "/home/admin/"
  }

  provisioner "shell" {
    inline = [
      "sudo yum install -y unzip",
      "sudo yum update unzip",
      "curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh",
      "sudo bash add-google-cloud-ops-agent-repo.sh --also-install",
      "pwd",
      "sudo mkdir -p ../csye6225/webapp",
      "sudo cp /home/admin/webapp.zip ../csye6225/webapp",
      "cd ../csye6225/webapp",
      "sudo unzip -q webapp.zip",
      "echo '****************************************************************************'",
      "ls target",
      "echo '****************************************************************************'",
    ]
  }

  provisioner "shell" {
    scripts = var.script_paths
  }
}
