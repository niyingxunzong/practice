#include "stdafx.h"
using namespace std;
#include <fstream>


int main()
{
	readInode(3);
	/*
	ifstream infile("disk.dat",ios::binary|ios::app);
	inode i ;
	infile.seekg(1024,ios::beg);    //移动文件指针
	infile.read((char*)&i,sizeof(i));
	cout<<i.getBlockNum()<<endl;
	*/
	/*
	ifstream inf("disk.dat",ios::binary|ios::app);
	superblock s ;
	inf.read((char*)&s,sizeof(s));
	cout<<s.getBlockNum()<<endl;
	*/
	/*
	ifstream infile("disk.dat",ios::binary);
	infile.read((char*)&sb,sizeof(sb));
	cout<<sb.getBlockNum()<<endl;
	*/
	superblock sb ;    //建立一个超级块对象，常驻内存

	
	printf("                 ********************************************\n");
	printf("                 *            欢迎使用磁盘管理系统          *\n");
	printf("                 *                                          *\n");
	printf("                 *              1、初始化磁盘               *\n");
	printf("                 *              2、创建用户                 *\n");
	printf("                 *              3、用户登录                 *\n");
    printf("                 *              4、退出系统                 *\n");
	printf("                 ********************************************\n");
	while(true)
	{
		bool flg = false;
		printf("请选择对应的序号:/>");
		int i = 0;
		cin>>i;
		switch(i)
		{
		case 1:
			//cout<<"你选了1"<<endl;
			initDisk(sb);
			break;
		case 2:
			cout<<"你选了2"<<endl;
			break;
		case 3:
			//cout<<"你选择了3"<<endl;
			if(userLogin(sb))    //登陆成功
			{
				while(true)
				{
					cout<<"处理登陆后的操作"<<endl;
					fileOpation();
					cout<<"请选择：/>";
					//char arr[10];
					int arr;
					cin>>arr;
					switch(arr)
					{
					case 1 :
						createFile(sb,"第一个文件");
						break;
					default:
						cout<<"输入错误,请重新输入!"<<endl;
						break;
					}
				}
			}
			break;
		case 4:
			cout<<"即将退出系统！"<<endl;
			exit(0);
			break;
		default :
			cout<<"选择错误重新选择！"<<endl;
			break;
		}
		//if(i==1||i==2||i==3||i==4)
		//	break;
		if(flg)
		{
			break;
		}
	}

	return 0;
}