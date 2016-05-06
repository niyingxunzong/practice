#!/usr/bin/env python
# -*- coding: utf-8 -*-

__author__ = 'liuqiang'


class UrlManager(object):
    def __init__(self):
        self.new_urls = set()
        self.old_urs = set()

    def add_new_url(self, url):
        if url is None:
            return
        if url not in self.new_urls and url not in self.old_urs:
            self.new_urls.add(url)

    def add_new_urls(self, urls):
        if urls is None or len(urls) == 0:
            return
        for url in urls:
            self.add_new_url(url)

    def has_new_url(self):
        return self.new_urls != 0

    def get_new_url(self):
        new_url = self.new_urls.pop()
        self.old_urs.add(new_url)
        print '共 %d 个url' % (len(self.new_urls))
        return new_url


if __name__ == '__main__':
    pass
