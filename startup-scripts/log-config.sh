pwd

# shellcheck disable=SC2164
# shellcheck disable=SC2232
sudo cd ../csye6225/webapp/
pwd
sudo ls
sudo cp ./startup-scripts/config.yaml /etc/google-cloud-ops-agent
pwd
# shellcheck disable=SC2164
cd ../../../var/log
pwd
sudo mkdir webapp
sudo chmod u+w /var/log/webapp
echo 'coppying config'



sudo vi /etc/google-cloud-ops-agent/config.yaml
sudo systemctl restart google-cloud-ops-agent