#include "stdafx.h"

#include <fstream>
using namespace std;

int MAX_INODE_BLOCK_NUM = ((MAX_INODE_NUM * SINGLE_INODE_SIZE)/BLOCK_SIZE);     //inode 节点所占用的磁盘块数目

/*初始化磁盘文件*/
int initDisk(superblock &sb)   
{
	ifstream infile("disk.dat",ios::binary);     //这种打开方式如果文件不存在不会新建，会报错，利用这个特点来检查模拟磁盘文件是否存在
	if(!infile)
	{
		cout<<"磁盘文件上不存在,模拟开始,磁盘文件正在创建..."<<endl;
		ofstream outfile("disk.dat",ios::binary);     //如果打开的文件不存在，就会新建一个
		if(outfile)     //文件创建并打开成功
		{
			cout<<"磁盘文件创建完成！"<<endl;
			while(true)
			{
				char c = 'Y';
				cout<<"是否格式化:(Y/N)";
				cin>>c;
				switch (c)
				{
				case 'Y':
					formatDisk(sb);
					break;
				case 'N':
					break;
				default:
					cout<<"选择错误！"<<endl;
				}
				if(c=='Y'||c=='N')    //如果用户正常选择了，退出无限循环
				{
					break;
				}		
			}
			outfile.close();
		}
		else     //模拟磁盘文件创建失败，此程序没有进行下去的意义了，程序退出
		{
			cout<<"init disk error !"<<endl;
			exit(0);
		}
	}
	else    //如果磁盘文件一开始就存在
	{
		infile.seekg(0,ios::end);    //文件指针指到文件末尾
		//cout<<infile.tellg()<<endl;     //
		if(!infile.tellg())
		{
			cout<<"磁盘文件尚未格式化，建议格式化..."<<endl;
		}
		else
		{
			//读入超级块总的数据
			infile.seekg(0,ios::beg);
			infile.read((char*)&sb,sizeof(sb));
			/*
			cout<<sb.getBlockNum()<<endl;
			cout<<"用户数："<<sb.getUserNum()<<endl;
			cout<<"第一个用户名为："<<sb.getUser()[0].getName()<<endl;
			*/
			cout<<"磁盘中存在数据，格式化请慎重..."<<endl;
		}
		infile.close();    //文件关闭
		while(true)
		{
			char c = 'Y';
			cout<<"是否--格式化:(Y/N)";
			cin>>c;
			switch (c)
			{
			case 'Y':
				formatDisk(sb);
				break;
			case 'N':
				break;
			default:
				cout<<"选择错误！"<<endl;
			}
			if(c=='Y'||c=='N')    //如果用户正常选择了，退出无限循环
			{
				break;
			}	
		}
	}
	return 1;
}

/*磁盘格式化*/
int formatDisk(superblock &sb)     //格式化过程将该写入磁盘的写入，并计算磁盘的一些记录
{
	cout<<"格式化开始!"<<endl;
	sb.setBlockNum(DISK_SIZE/BLOCK_SIZE);     //所有磁盘块数
	sb.setFreeBlockNum(DISK_SIZE/BLOCK_SIZE);    //空闲磁盘块数
	sb.setInodeNum(MAX_INODE_NUM);    //所有的inode的数目
	sb.setFreeInodeNum(MAX_INODE_NUM);    //空闲inode的数目
	sb.setInodeStartBlockNum(1);     //假设从第二号块开始存放，也就是1号块
	sb.setInodeBlockNum(MAX_INODE_BLOCK_NUM);    //宏定义计算出的所需的块数
	sb.setDirectoryInodeId(0);     //这里设置第一个inode 是目录文件的inode
	sb.setUserNum(0);    //当前尚没有用户

	sb.initFreeBlockBitMap();    //初始化空闲块位示图
	sb.initFreeInodeBitMap();    //初始化空闲节点位示图

	//下面开始操作磁盘文件
	ofstream outfile("disk.dat",ios::binary);
	outfile.seekp(0,ios::beg);    //文件指针指向开始位置 

	//超级块写入磁盘之前，修改磁盘空闲空间的数目   和标记磁盘的位示图
	sb.setFreeBlockNum(sb.getFreeBlockNum()-1);    //超级块本身占据一个磁盘块  所以空白块少了一个
	sb.changeBit(sb.getFreeBlockBitMap(),0);    //将位示图第一个数据位记为1 表示已被占用
	//cout<<"空闲块位示图："<<sb.getFreeBlockBitMap()[0]<<endl;


	
	//创建目录文件，并为其创建inode节点   修改inode节点位示图   写在第二个磁盘块中
	inode dirInode;
	dirInode.setId(0);    //把0号inode用来做目录文件的id
	dirInode.setBlockNum(0);

	for(int i = MAX_INODE_BLOCK_NUM+1;i<sb.getBlockNum();i++)    //真正的文件存储从超级块和inode块后开始
	{
		//从这之后的块中找出一块空闲的块，分配给目录文件
		if(!sb.isOne(sb.getFreeInodeBitMap(),i))
		{
			//cout<<"cao ---------"<<i<<endl;
			//找到空闲磁盘块，占有它 并修改其位示图中的信息
			sb.changeBit(sb.getFreeInodeBitMap(),i);    //位示图索引从0开始
			sb.setFreeBlockNum(sb.getFreeBlockNum()-1);    //空闲磁盘块数减一
			sb.setFreeInodeNum(sb.getFreeInodeNum()-1);    //空闲节点数减一
			dirInode.addAddr(i);    //把地址块号添加到inode中
			break;    //只找一个，找到后跳出循环
		}
	}
	sb.changeBit(sb.getFreeInodeBitMap(),0);    //将位示图第一个数据位记为1 表示此inode已被占用

	//cout<<"判断某位是否为1："<<sb.isOne(sb.getFreeBlockBitMap(),44)<<endl;

	//在格式化时创建一个超级用户root 密码root 
	user u("root","root");
	sb.addUser(u);
	//cout<<"文件指针位置："<<outfile.tellp()<<endl;
	outfile.write((char*)&sb,sizeof(sb));    //将超级块写入磁盘块中第一块     也就是0号块
	//cout<<"sizeof(sb)"<<sizeof(sb)<<endl;
	//cout<<"文件指针位置："<<outfile.tellp()<<endl;
	


	//将inode信息写入到磁盘中   这个因为是在模拟磁盘所以写入是需要计算文件指针的位置
	int offset = BLOCK_SIZE * 1 + dirInode.getId()*sizeof(inode);     //
	outfile.seekp(0,ios::beg);    //文件指针指向开始位置 

	//cout<<"offect "<<offset<<endl;
	//cout<<"文件指针位置："<<outfile.tellp()<<endl;
	outfile.seekp(offset,ios::beg);    //文件指针移动到inode所在位置
	//cout<<"文件指针位置："<<outfile.tellp()<<endl;
	outfile.write((char*)&dirInode,sizeof(dirInode));
	//cout<<"sizeof(dirInode)"<<sizeof(dirInode)<<endl;
	//cout<<"文件指针位置："<<outfile.tellp()<<endl;

	outfile.close();

	/*
	inode ii ;
	ifstream ifs("disk.dat",ios::binary|ios::app);
	ifs.seekg(offset,ios::beg);
	ifs.read((char*)&ii,sizeof(ii));
	cout<<ii.getBlockNum()<<endl;
	*/

	cout<<"格式化完成!"<<endl;
	return 1;
}

/*文件创建*/
int createFile(superblock &sb,char * filename)
{
	//首先查找超级块中的 空闲块和空闲节点数是否已经占满
	if(sb.getFreeBlockNum()<=0 || sb.getFreeInodeNum()<=0)
	{
		cout<<"当前空间已满，不可分配！"<<endl;
		return 0;
	}
	else    //进行磁盘分配和inode节点的分配
	{
		//先分配inode节点
		for(int j = 0;j<sb.getInodeNum();j++)
		{
			//判断位示图中这位是否空闲
			bool isTrue = false;
			isTrue = sb.isOne(sb.getFreeInodeBitMap(),j);
			if(!isTrue)    //如果空闲，占用它
			{
				cout<<"创建文件函数中此次分配的i节点的节点号："<<j<<endl;
				sb.changeBit(sb.getFreeInodeBitMap(),j);     //修改空闲节点位示图
				inode newInode ;
				newInode.setId(j);    //id号是从0开始计数的
				newInode.setFileSize(0);    //文件为空
				newInode.setBlockNum(0);
				for(int i = MAX_INODE_BLOCK_NUM+1;i<sb.getBlockNum();i++)    //真正的文件存储从超级块和inode块后开始
				{
					//从这之后的块中找出一块空闲的块，分配给文件
					if(!sb.isOne(sb.getFreeBlockBitMap(),i))
					{
						cout<<"创建文件函数中此次分配的空闲块的节点号："<<i<<endl;
						//找到空闲磁盘块，占有它 并修改其位示图中的信息
						sb.changeBit(sb.getFreeBlockBitMap(),i);    //位示图索引从0开始
						sb.setFreeBlockNum(sb.getFreeBlockNum()-1);    //空闲磁盘块数减一
						sb.setFreeInodeNum(sb.getFreeInodeNum()-1);    //空闲节点数减一
						newInode.addAddr(i);    //把地址块号添加到inode中
						break;
					}
				}

				//将超级块的信息写入文件
				ofstream outfile("disk.dat",ios::binary);
				outfile.seekp(0,ios::beg);    //文件指针放在开始位置
				cout<<"当前文件指针："<<outfile.tellp()<<endl;
				outfile.write((char*)&sb,sizeof(sb));
				cout<<"写入文件大小："<<sizeof(sb)<<endl;
				cout<<"当前文件指针："<<outfile.tellp()<<endl;

				//将inode信息写入到磁盘中   这个因为是在模拟磁盘所以写入是需要计算文件指针的位置
				int offset = BLOCK_SIZE * 1 + newInode.getId()*sizeof(inode);     //
				cout<<"offset:"<<offset<<endl;
				cout<<"当前文件指针："<<outfile.tellp()<<endl;
				outfile.seekp(offset,ios::beg);    //文件指针移动到inode所在位置
				cout<<"指针偏移之后当前文件指针："<<outfile.tellp()<<endl;
				outfile.write((char*)&newInode,sizeof(newInode));
				cout<<"写完文件后当前文件指针："<<outfile.tellp()<<endl;
				outfile.close();

				ifstream inf("disk.dat",ios::binary);
				inode i ;
				inf.seekg(offset,ios::beg);
				inf.read((char*)&i,sizeof(i));
				cout<<"id--"<<i.getId()<<endl;

				//writeToFCB(filename,newInode.getUser(),newInode.getId());    //将文件信息写入目录表中

				break;    //跳出循环
			}
		}
	}
}

/*写入FCB*/
int writeToFCB(char * filename ,char * owener,int inodeId)
{
	FCB fcb ;    //
	fcb.setFileName(filename);
	fcb.setOwener(owener);
	fcb.setInodeId(inodeId);

	//读入目录文件所对应的inode节点，然后查找inode文件的存放  磁盘块
	int offset = BLOCK_SIZE*1 ;
	ifstream infile("disk.dat",ios::app);
	inode i ;
	infile.seekg(offset,ios::beg);    //移动文件指针
	infile.read((char*)&i,sizeof(i));
	infile.close();
	
	cout<<"文件长度："<<i.getFileSize()<<endl;
	cout<<"文件所占的块数："<<i.getBlockNum()<<endl;
	cout<<"文件所在磁盘块号:"<<i.getAddr()[0]<<endl;    //这里假设目录文件只占用一块磁盘块

	//将FCB写入目录文件
	ofstream outfile("disk.dat",ios::binary);
	offset = i.getAddr()[0] * BLOCK_SIZE + inodeId * sizeof(fcb);
	outfile.seekp(0,ios::beg);
	outfile.seekp(offset,ios::beg);
	outfile.write((char*)&fcb,sizeof(fcb));
	outfile.close();

	return 1;
}

//读取inode
inode * readInode(int inodeId)
{
	int offset = BLOCK_SIZE*1 + inodeId * sizeof(inode) ;

	cout<<"offset:"<<offset<<endl;
	ifstream infile("disk.dat",ios::binary);
	inode i ;
	infile.seekg(offset,ios::beg);    //移动文件指针
	infile.read((char*)&i,sizeof(i));

	cout<<""<<i.getBlockNum()<<endl;
	cout<<"___"<<i.getAddr()[0]<<endl;

	return &i;
}

/*创建用户*/
int createAccount(char * username ,char * password,superblock &sup)
{
	user newUser(username,password);
	sup.addUser(newUser);
	return 1;
}

/*用户登录*/
int userLogin(superblock &sb)
{
	cout<<"当前系统用户如下，请选择：/>"<<endl;
	for(int i=0;i<sb.getUserNum();i++)
	{
		cout<<sb.getUser()[i].getName()<<endl;
	}
	cout<<"请选择登录用户名/>";
	char username[USER_NAME_SIZE] = "";
	char pass[USER_PASS_SIZE] = "" ;
	cin>>username;
	user u ;    //声明一个user对象暂时存放用户信息
	bool isExit = false;    //是否存在此用户
	for(int j = 0;j<sb.getUserNum();j++)
	{
		if(strcmp(sb.getUser()[j].getName(),username)==0)
		{
			u.setName(username);
			u.setPass(sb.getUser()[j].getPass());
			isExit = true;
		}
	}
	while(!isExit)
	{
		cout<<"用户名错误，请重新选择/>";
		cin>>username;
		for(int j = 0;j<sb.getUserNum();j++)
		{
			if(strcmp(sb.getUser()[j].getName(),username)==0)
			{
				u.setName(username);
				u.setPass(sb.getUser()[j].getPass());
				isExit = true;
			}
		}
	}

	if(isExit)
	{
		while(true)    //登录验证
		{
			cout<<"请输入密码/>";
			cin>>pass;
			if(strcmp(pass,u.getPass())==0)    //密码输入正确
			{
				cout<<"登录成功---"<<endl;
				system("cls");
				//fileOpation();
				break;
			}
			else    //密码输入错误
			{
				cout<<"密码错误重新输入!"<<endl;
			}
		}
	}
	return 1;
}

/*文件操作选项*/
void fileOpation()
{
	cout<<"------------------1.创建文件------------------"<<endl;
	cout<<"------------------2.删除文件------------------"<<endl;
	cout<<"------------------3.打开文件------------------"<<endl;
	cout<<"------------------4.写入文件------------------"<<endl;
	cout<<"------------------5.读入文件------------------"<<endl;
	cout<<"-----------------6.修改文件名------------------"<<endl;

}