def call(env = 'prod') {
    echo "Running Ansible for environment: ${env}"
    sh """
        /Users/syedrehan/anaconda3/bin/ansible-playbook \
        env/${env}/site.yml \
        -i env/${env}/inventory \
        --private-key ~/.ssh/my_ubuntu.pem \
        -u ubuntu \
        -b \
        -vvvv
    """
}

