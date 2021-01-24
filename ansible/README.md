# Ansible sample playbooks

### Pre-requisites

Install [ansible](http://www.ansible.com/) on your workstation.

Try this with Ubuntu Saucy: <code>$> sudo apt-get -t saucy-backports install ansible</code>


### Run

    $> ansible-playbook playbook.yml [--check] -i hosts
    
with **--check** option for dry-run play


### Content

* **workstations** - a sample playbook to quickly rebuild my development workstation
