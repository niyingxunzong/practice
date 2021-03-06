#!/usr/bin/env python
# -*- coding: utf-8 -*-


import html_downloader
import html_outputer
import html_parser
import url_manager

__author__ = 'liuqiang'


class SpiderMain(object):
    def __init__(self):
        self.urls = url_manager.UrlManager()
        self.downloader = html_downloader.HtmlDownloader()
        self.parser = html_parser.HtmlParser()
        self.outputer = html_outputer.HtmlOutputer()

    def craw(self, root_url):
        count = 1
        self.urls.add_new_url(root_url)
        while self.urls.has_new_url():
            try:
                new_url = self.urls.get_new_url()
                print 'craw %d:%s' % (count, new_url)
                html_content = self.downloader.download(new_url)
                new_urls, new_data = self.parser.parse(new_url, html_content)
                self.urls.add_new_urls(new_urls)
                self.outputer.collect_data(new_data)
                count += 1

                if count == 1000:
                    break
            except:
                print 'craw failed'

        self.outputer.output_html()


if __name__ == '__main__':
    url = "http://baike.baidu.com/link?url=ujKXFby4cF3ulAnlClPAqfaG0hq8RUZHIB35HR4kKptI0E5LKumrwLcf9nhpz5mJpMeL5NdvYIjK_KWWqEGhm_"
    obj_spider = SpiderMain()
    obj_spider.craw(url)
