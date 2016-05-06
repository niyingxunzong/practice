#include"stdafx.h"
using namespace std;



//用户类函数
user::user()
{
	strcpy(this->user_name,"");
	strcpy(this->pass_word,"");
}
user::user(char * username,char * password)
{
	strcpy(this->user_name,username);
	strcpy(this->pass_word,password);
}
char * user::getName()   //获取用户名
{
	return this->user_name;
}
void  user::setName(char * username)   //重新设置用户名
{
	strcpy(this->user_name,username);
}
char * user::getPass()    //获取用户密码
{
	return this->pass_word;
}
void user::setPass(char * password)
{
	strcpy(this->pass_word,password);
}

/*超级块中的操作函数*/
superblock::superblock()    //初始化
{
	/**/
	this->blocknum = 0;
	this->directoryInodeId = 0;
	//this->freeblock = NULL;
	this->freeblocknum = 0 ;
	//this->freeinode = NULL;
	this->freeInodeNum = 0;
	this->inodeBlockNum = 0;
	this->inodenum = 0;
	this->inodeStartBlockNum = 0;
	this->userNum = 0 ;
	
}
/*设置盘块总数*/
int superblock::setBlockNum(int num)
{
	this->blocknum = num;
	return 1;
}
/*获取盘块数*/
int superblock::getBlockNum()
{
	return this->blocknum;
}
/*设置空闲盘块数*/
int superblock::setFreeBlockNum(int num)
{
	this->freeblocknum = num ;
	return 1;
}
/*获取空闲盘块数*/
int superblock::getFreeBlockNum()
{
	return this->freeblocknum;
}
/*设置节点总数*/
int superblock::setInodeNum(int num)
{
	this->inodenum = num;
	return 1;
}
/*获取节点总数*/
int superblock::getInodeNum()
{
	return this->inodenum;
}
/*设置空闲节点总数*/
int superblock::setFreeInodeNum(int num)
{
	this->freeInodeNum = num;
	return 1;
}
/*获取空闲节点总数*/
int superblock::getFreeInodeNum()
{
	return this->freeInodeNum;
}
/*设置存放inode的盘块的起始号*/
int superblock::setInodeStartBlockNum(int num)
{
	this->inodeStartBlockNum = num;
	return 1;
}
/*获取存放inode的盘块的起始盘号*/
int superblock::getInodeStartBlockNum()
{
	return this->inodeStartBlockNum;
}
/*设置inode所占用的盘块数目*/
int superblock::setInodeBlockNum(int num)
{
	this->inodeBlockNum = num;
	return 1;
}
/*获取inode所占用的盘块数目*/
int superblock::getInodeBlockNum()
{
	return this->inodeBlockNum;
}
/*设置当前系统中的用户数*/
int superblock::setUserNum(int num)
{
	this->userNum = num;
	return 1;
}
/*获取当前系统中存在的用户数*/
int superblock::getUserNum()
{
	return this->userNum;
}
/*设置目录文件的inode的id号*/
int superblock::setDirectoryInodeId(int num)
{
	this->directoryInodeId = num;
	return 1;
}
/*获取目录文件的inode的id号*/
int superblock::getDirectoryInodeId()
{
	return this->directoryInodeId;
}
//初始化空闲块的位示图
void superblock::initFreeBlockBitMap()    
{
	for(int i =0 ;i<BLOCK_NUM/32;i++)
		this->freeblockbitmap[i] = (unsigned int)0;
}
//初始化空闲节点的位示图
void superblock::initFreeInodeBitMap()
{
	for(int i =0 ;i<BLOCK_NUM/32;i++)
		this->freeinodebitmap[i] = (unsigned int)0;
}
void superblock::setFreeBlockBitMap()   //
{

}
unsigned int * superblock::getFreeBlockBitMap()    //获取空闲块位示图数组
{
	return this->freeblockbitmap;
}
void superblock::setFreeInodeBitMap()
{
	
}
unsigned int * superblock::getFreeInodeBitMap()    //获取空闲节点位示图数组
{
	return this->freeinodebitmap;
}
/*添加用户*/
int superblock::addUser(user &u)
{
	//cout<<this->userNum<<endl;
	if(this->userNum < MAX_USER_SIZE)    //如果用户数还没有超过最大用户数
	{
		this->users[this->userNum].setName(u.getName());
		this->users[this->userNum].setPass(u.getPass());
		this->userNum = this->userNum+1;
		return 1;
	}
	else
	{
		cout<<"不可创建更多用户！"<<endl;
		return 0;
	}
}
/*获取所有的用户*/
user * superblock::getUser()
{
	return this->users;
}
/*变换int类型的数的某位二进制位    如果原来是0 变成1 如果原来是1 变成0 */
void superblock::changeBit(unsigned int*arr,int index)    //参数的含义，要更改的块号   块是从0开始计数的 index从0开始计数
{
	//控制数组是unsigned int 类型，占四个字节，也就是说总共有32位，即：数组中的每个元素控制32个数据块

	int remainder = index % 32 ;    //余数  位移（要修改的位）
	int shang = index / 32 ;     //商   第几个数组元素
	unsigned int temp = arr[shang];

	//从0开始计算位数，
	temp^=(1<<remainder) ;   //    转换   因为从0开始计算位数，   temp^=(1<<remainder-1) ; （这个是从1开始计数）
	arr[shang] = temp ;
}
/*想判断十进制数 d 的二进制第n位是否是1*/
bool superblock::isOne(unsigned int *arr,int index)    //下标从1开始，记住不是0
{
	/*
		如果想判断十进制数 d 的二进制第n位是否是1，应该用以下的数m来进行&运算
		m = 1 << n-1 （n是大于0的整数)
		以上公式
		n=1的时候m=1
		n=2的时候m=2
		n=3的时候m=4
		n=4的时候m=8
		...
		例子10 & 2所述的是n=2的情况。
	*/

	int remainder = index % 32 ;    //余数  位移（要修改的位）
	int shang = index / 32 ;     //商   第几个数组元素
	unsigned int temp = arr[shang];

	int m = 1 << remainder;
	int result = temp & m;
	if(result == m)
		return true;
	else
		return false;
}


//以下是inode的成员函数
inode::inode()
{
	for(int i=0;i<DIRECT_BLOCK_SIZE;i++)     //初始化地址空间
		this->addr[i] = -1;
	this->addr_index1 = -1;
	this->addr_index2 = -1;
}
void inode::setId(int num)
{
	this->id = num;
}
int inode::getId()
{
	return this->id;
}
void inode::setFileSize(int size)
{
	this->fileSize = size;
}
int inode::getFileSize()
{
	return this->fileSize;
}
void inode::setBlockNum(int num)
{
	this->blockNum = num;
}
int inode::getBlockNum()
{
	return this->blockNum;
}
void inode::initAddr()
{
	
}
int * inode::getAddr()
{
	return this->addr;
}
void inode::addAddr(int num)    //添加文件 磁盘块
{
	if(this->blockNum<DIRECT_BLOCK_SIZE)
	{
		this->addr[this->blockNum] = num;
		this->blockNum = this->blockNum+1;     //盘块记录数加1
	}else    //如果此时这个文件所消耗的磁盘块已经大于直接盘块索引空间了
		//那么采用二级索引
	{
		
	}	
}
void inode::setUser(char * username)
{
	strcpy(this->owener ,username);
}
char * inode::getUser()
{
	return this->owener;
}
void inode::setChangeTime(char * time)
{
	strcpy(this->changeTime ,time);
}
char * inode::getChangeTieme()
{
	return this->changeTime;
}

///FCB   操作函数
FCB::FCB()
{
	
}
void FCB::setFileName(char * name)
{
	strcpy(this->filename,name);
}
char * FCB::getFileName()
{
	return this->filename;
}
void FCB::setOwener(char * owener)
{
	strcpy(this->owener,owener);
}
char * FCB::getOwener()
{
	return this->owener;
}
void FCB::setInodeId(int num)
{
	this->inodeId = num;
}
int FCB::getInodeId()
{
	return this->inodeId;
}

