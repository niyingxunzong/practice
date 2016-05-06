#coding=UTF-8
from django.db import models

from django.contrib.auth.models import User

# Create your models here.
    

class BlogComment(models.Model):
    '''博客评论'''
    content = models.TextField()    #评论内容
    name = models.CharField(max_length=30)
    email = models.EmailField('e-mail',blank=True)
    
    child_comments = models.ManyToManyField("self",blank=True,null=True)


class BlogEntity(models.Model):
    '''Blog Entity !'''
    title = models.CharField(max_length=100)
    author = models.ForeignKey(User)    #一篇文章只有一个作者，一个作者可以写n多文章，author.blogentity_set.all()
    body_text = models.TextField()
    pub_date = models.DateTimeField(auto_now_add=True, blank=True)
    link_view = models.IntegerField()    #浏览量
    comments = models.ManyToManyField(BlogComment)    #定义一个多对多的评论关系,这个在初始化BlogEntity的时候可以不赋值
    
    def __unicode__(self):
        return self.title,self.author
    
    class Meta:
        ordering = ['-pub_date']
    


