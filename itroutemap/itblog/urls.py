#coding=UTF-8
'''
Created on 2014年5月23日

@author: LiuQiang
'''

from django.conf.urls import patterns , url

from django.contrib import admin
admin.autodiscover()

from itblog import views

urlpatterns = patterns('',
    url(r'^$', views.index),
    url(r'blogpost$', views.req_method_splitter,{'GET':views.blogpost_page_get,'POST':views.blogpost_page_post}),
    
)



