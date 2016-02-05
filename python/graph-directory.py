from __future__ import print_function
import httplib2
import os

from apiclient import discovery
from apiclient import errors
import oauth2client
from oauth2client import client
from oauth2client import tools

from datetime import datetime as dt
import functools
import graphviz as gv

digraph = functools.partial(gv.Digraph, format='svg')
groups_done = []

try:
    import argparse
    flags = argparse.ArgumentParser(parents=[tools.argparser]).parse_args()
except ImportError:
    flags = None

SCOPES = 'https://www.googleapis.com/auth/admin.directory.user https://www.googleapis.com/auth/admin.directory.group'
CLIENT_SECRET_FILE = 'client_secret.json'
APPLICATION_NAME = 'Directory API Python Quickstart'

# Google apps domain name to filter/format email addresses
DOMAIN_NAME = 'bonitasoft.com'


def get_credentials():
    """Gets valid user credentials from storage.

    If nothing has been stored, or if the stored credentials are invalid,
    the OAuth2 flow is completed to obtain the new credentials.

    Returns:
        Credentials, the obtained credential.
    """
    home_dir = os.path.expanduser('~')
    credential_dir = os.path.join(home_dir, 'work', 'python', '.credentials')
    if not os.path.exists(credential_dir):
        os.makedirs(credential_dir)
    credential_path = os.path.join(credential_dir,
                                   'admin-directory_v1-python-quickstart.json')

    store = oauth2client.file.Storage(credential_path)
    credentials = store.get()
    if not credentials or credentials.invalid:
        flow = client.flow_from_clientsecrets(CLIENT_SECRET_FILE, SCOPES)
        flow.user_agent = APPLICATION_NAME
        if flags:
            credentials = tools.run_flow(flow, store, flags)
        else: # Needed only for compatibility with Python 2.6
            credentials = tools.run(flow, store)
        print('Storing credentials to ' + credential_path)
    return credentials


def get_all_groups(service):
    groups = []
    page_token = None
    param = { 'customer':'my_customer', 'fields':'nextPageToken,groups/email' }
    while True:
        try:
            if page_token:
                param['pageToken'] = page_token
            page_groups = service.groups().list(**param).execute()
            if 'groups' in page_groups:
                groups.extend(page_groups['groups'])
            page_token = page_groups.get('nextPageToken')
            if not page_token:
                break
        except errors.HttpError, error:
          print('An error occurred: %s' % error)
          break
    return [g for g in groups if 'email' in g]


def get_all_users(service):
    users = []
    page_token = None
    param = { 'customer':'my_customer', 'fields':'nextPageToken,users/primaryEmail' }
    while True:
        try:
            if page_token:
                param['pageToken'] = page_token
            page_users = service.users().list(**param).execute()
            if 'users' in page_users:
                users.extend(page_users['users'])
            page_token = page_users.get('nextPageToken')
            if not page_token:
                break
        except errors.HttpError, error:
          print('An error occurred: %s' % error)
          break
    return users


def graph_group_tree(service, graph, group, level):
    if groups_done.count(group['email']) > 0:
        return graph

    indentString = '  '
    groupKey = group['email']
    print('%s%s (GROUP)' % (indentString * level, groupKey))

    members = get_members(service, group)
    groupLabel = get_group_label(group, members)
    graph = add_node(graph, get_name(groupKey), 'group', label=groupLabel)
    if members and len(members) > 0:
        for m in members:
            if 'email' in m and m['email'].endswith('@%s' % DOMAIN_NAME):
                if m['type'] == 'GROUP':
                    graph = graph_group_tree(service, graph, m, level+1)
                else:
                    print('%s%s (%s)' % (indentString * (level+1), m['email'], m['type']))
                    graph = add_node(graph, get_name(m['email']), 'user')
                graph = add_edge(graph, get_name(groupKey), get_name(m['email']))
    else:
        print('%sNo members found.' % indentString * (level+1))
    groups_done.append(groupKey)
    return graph
    
    
def get_members(service, group):
    groupKey = group['email']
    members = []
    page_token = None
    param = { 'groupKey':groupKey, 'fields':'nextPageToken,members(email,type)' }
    while True:
        try:
            if page_token:
                param['pageToken'] = page_token
            page_members = service.members().list(**param).execute()
            if 'members' in page_members:
                members.extend(page_members['members'])
            page_token = page_members.get('nextPageToken')
            if not page_token:
                break
        except errors.HttpError, error:
          print('An error occurred: %s' % error)
          break
    return members


def get_name(email):
    return email.replace('@%s' % DOMAIN_NAME, '').lower()


def get_group_label(group, members):
    return '%s (%s)' % (get_name(group['email']), len(members))


def add_node(graph, name, type, label=None):
    attrs = {}
    if type == 'group':
        attrs = {
            'fontname': 'Verdana',
            'fontsize': '14.0',
            'shape': 'ellipse',
            'style': 'filled',
            'fillcolor': 'lightblue',
            'width': '0.5'
        }
    if type == 'user':
        attrs = {
            'fontname': 'Verdana',
            'fontsize': '12.0',
            'shape': 'box',
            'margin': '0.08',
            'height': '0'
        }
    graph.node(name, label=label, **attrs)
    return graph


def add_edge(graph, parent, child):
    graph.edge(parent, child)
    return graph


def main():
    credentials = get_credentials()
    http = credentials.authorize(httplib2.Http())
    service = discovery.build('admin', 'directory_v1', http=http)

    # Initialize graph
    graph = digraph()
    graph.edge_attr.update(arrowsize='0.7')
    graph.graph_attr.update(nodesep='0.1', ranksep='2.5', bgcolor='lavender')

    # Browse all groups
    groups = get_all_groups(service)
    for g in groups:
        graph = graph_group_tree(service, graph, g, 0)
    
    # Browse all users
    users = get_all_users(service)
    for u in users:
        graph = add_node(graph, get_name(u['primaryEmail']), 'user')

    graph.render('graph/directory-%s-%s' % (DOMAIN_NAME, dt.now().strftime('%Y%m%d_%H%M')))


if __name__ == '__main__':
    main()
