#coding=UTF-8
from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

from django.contrib.auth.views import login,logout

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'itroutemap.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url(r'^$', include('itblog.urls')),
    url(r'^blog/', include('itblog.urls')),
    url(r'accounts/login/$',login),    ###利用系统自带的登陆
    url(r'accounts/logout/$',logout,{'next_page':'/accounts/login/'}),    ##利用系统自带的额登出，退出后重定向到登陆页面
)


