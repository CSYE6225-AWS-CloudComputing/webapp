pwd

# shellcheck disable=SC2164
cd ../csye6225/webapp/
ls ./startup-scripts
sudo cp ./startup-scripts/config.yaml /etc/google-cloud-ops-agent
pwd
cd ../../../var/log
pwd
sudo mkdir webapp
sudo chmod u+w /var/log/webapp
echo 'coppying config'



sudo vi /etc/google-cloud-ops-agent/config.yaml
sudo systemctl restart google-cloud-ops-agent