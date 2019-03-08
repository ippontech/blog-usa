import os

post_meta = {}

for post_file_name in os.listdir('posts'):
    with open(f'posts/{post_file_name}') as post:
        length = len(post.read().split(' '))

        post_meta[post_file_name[:-3]] = length

post_list = [(value, key) for key, value in post_meta.items()]

print('\n'.join(map(str, sorted(post_list))))
