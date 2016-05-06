#!/usr/bin/env python
# -*- coding: utf-8 -*-

__author__ = 'liuqiang'


class HtmlOutputer(object):
    def __init__(self):
        self.data = []

    def collect_data(self, new_data):
        if new_data is None:
            return
        self.data.append(new_data)

    def output_html(self):
        f_out = open("output.html", 'w')
        f_out.write("<html>")
        f_out.write("<body>")
        f_out.write("<table>")
        # ascii
        for d in self.data:
            f_out.write("<tr>")
            f_out.write("<td>%s</td>" % d['url'])
            f_out.write("<td>%s</td>" % d['title'].encode('utf-8'))
            f_out.write("<td>%s</td>" % d['summary'].encode('utf-8'))
            f_out.write("</tr>")
        f_out.write("</table>")
        f_out.write("</body>")
        f_out.write("</html>")
        f_out.close()


if __name__ == '__main__':
    pass
