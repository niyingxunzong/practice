#!/usr/bin/env python
# -*- coding: utf-8 -*-

import urllib2

__author__ = 'liuqiang'


class HtmlDownloader(object):
    def download(self, url):
        if url is None:
            return None
        response = urllib2.urlopen(url)
        if response.getcode() != 200:
            return None

        return response.read()


if __name__ == '__main__':
    print HtmlDownloader().download("http://www.baidu.com")
