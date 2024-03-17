chmod u+w /var/log/webapp/myapp.log

# shellcheck disable=SC2164
cd ../csye6225/webapp/
sudo cp ./startup-scripts/config.yaml /etc/google-cloud-ops-agent/

sudo vi /etc/google-cloud-ops-agent/config.yaml
sudo systemctl restart google-cloud-ops-agent