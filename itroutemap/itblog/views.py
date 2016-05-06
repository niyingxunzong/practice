#coding=UTF-8
# Create your views here.
from django.template import loader,Context,RequestContext    #template is a package
from django.http import HttpResponse,Http404,HttpResponseRedirect

from django.shortcuts import render_to_response
from django.contrib.auth.decorators import login_required

from models import BlogComment,BlogEntity
from itblog.forms import BlogPostForm



import datetime

def req_method_splitter(request,GET=None,POST=None):
    ''' 分离 是post请求还是get请求，GET get请求的view处理函数，POST post请求的视图处理函数'''
    if request.method == 'GET' and GET is not None:
        return GET(request)
    elif request.method == 'POST' and POST is not None:
        return POST(request)
    raise Http404

@login_required    ###登陆权限，要求，不登陆不许访问这个视图
def blogpost_page_get(request):
    '''用get方法请求发布博客页面，是第一次请求'''
    assert request.method == 'GET'
    form = BlogPostForm()
    return render_to_response('blogpost.html',context_instance=RequestContext(request,{'form':form}))

@login_required
def blogpost_page_post(request):
    '''用post方法请求发布博客页面，是第一次请求'''
    assert request.method == 'POST'
    
    form = BlogPostForm(request.POST)
    if form.is_valid():    #确定前台穿过来的数据是否合法
        cleaned_data = form.cleaned_data    #如果数据合法，所有数据组成一个字典
    
        author = request.user
        pub_date = datetime.datetime.now()    
        link_view = 0  #浏览量设置为0
        blog = BlogEntity(title=cleaned_data['title'],
                          author=author,
                          body_text=cleaned_data['content'],
                          pub_date=pub_date,
                          link_view=link_view)
        blog.save()
        return HttpResponseRedirect('/blog/')    #重定向到了首页
    else:    #数据不合法，重定向到发布页面，注意form对象会自带错误信息
        return render_to_response('blogpost.html',context_instance=RequestContext(request,{'form':form}))

# def index(request):
#     '''首页'''
    #blogs = BlogEntity.objects.all()
    #return render_to_response('index.html',{'blogs':blogs})

ONE_PAGE_OF_DATA = 2    #分页查询一页的数据量

def index(request):
    try:
        curPage = int(request.GET.get('curPage', '1'))
        allPage = int(request.GET.get('allPage','1'))
        pageType = str(request.GET.get('pageType', ''))
    except ValueError:
        curPage = 1
        allPage = 1
        pageType = ''

    #判断点击了【下一页】还是【上一页】
    if pageType == 'pageDown':
        curPage += 1
    elif pageType == 'pageUp':
        curPage -= 1

    startPos = (curPage - 1) * ONE_PAGE_OF_DATA   #从第几条开始取数据
    endPos = startPos + ONE_PAGE_OF_DATA    ##到第几条结束
    blogs = BlogEntity.objects.all()[startPos:endPos]

    if curPage == 1 and allPage == 1: #标记1
        allPostCounts = BlogEntity.objects.count()
        allPage = allPostCounts / ONE_PAGE_OF_DATA
        remainPost = allPostCounts % ONE_PAGE_OF_DATA
        if remainPost > 0:
            allPage += 1

    return render_to_response("index.html",{'blogs':blogs, 'allPage':allPage, 'curPage':curPage},context_instance=RequestContext(request))



