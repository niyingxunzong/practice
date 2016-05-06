#coding=UTF-8
'''
Created on 2014年5月24日

@author: LiuQiang
'''

from django import forms

class BlogPostForm(forms.Form):
    '''from 中的字段默认都是必须填写的，这个blog发布表单暂时先只放这两个字段'''
    title = forms.CharField(max_length=100)
    content = forms.CharField(widget=forms.Textarea)

if __name__ == '__main__':
    pass