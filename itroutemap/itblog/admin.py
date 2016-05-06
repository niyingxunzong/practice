#coding=UTF-8
from django.contrib import admin

# Register your models here.

from itblog.models import BlogComment,BlogEntity

admin.site.register(BlogComment)
admin.site.register(BlogEntity)
