def call(env = 'prod') {
    echo "Running Ansible for environment: ${env}"
    sh """
        ansible-playbook env/${env}/site.yml -i env/${env}/inventory
    """
}

