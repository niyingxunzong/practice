#include"stdafx.h"
using namespace std;



//�û��ຯ��
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
char * user::getName()   //��ȡ�û���
{
	return this->user_name;
}
void  user::setName(char * username)   //���������û���
{
	strcpy(this->user_name,username);
}
char * user::getPass()    //��ȡ�û�����
{
	return this->pass_word;
}
void user::setPass(char * password)
{
	strcpy(this->pass_word,password);
}

/*�������еĲ�������*/
superblock::superblock()    //��ʼ��
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
/*�����̿�����*/
int superblock::setBlockNum(int num)
{
	this->blocknum = num;
	return 1;
}
/*��ȡ�̿���*/
int superblock::getBlockNum()
{
	return this->blocknum;
}
/*���ÿ����̿���*/
int superblock::setFreeBlockNum(int num)
{
	this->freeblocknum = num ;
	return 1;
}
/*��ȡ�����̿���*/
int superblock::getFreeBlockNum()
{
	return this->freeblocknum;
}
/*���ýڵ�����*/
int superblock::setInodeNum(int num)
{
	this->inodenum = num;
	return 1;
}
/*��ȡ�ڵ�����*/
int superblock::getInodeNum()
{
	return this->inodenum;
}
/*���ÿ��нڵ�����*/
int superblock::setFreeInodeNum(int num)
{
	this->freeInodeNum = num;
	return 1;
}
/*��ȡ���нڵ�����*/
int superblock::getFreeInodeNum()
{
	return this->freeInodeNum;
}
/*���ô��inode���̿����ʼ��*/
int superblock::setInodeStartBlockNum(int num)
{
	this->inodeStartBlockNum = num;
	return 1;
}
/*��ȡ���inode���̿����ʼ�̺�*/
int superblock::getInodeStartBlockNum()
{
	return this->inodeStartBlockNum;
}
/*����inode��ռ�õ��̿���Ŀ*/
int superblock::setInodeBlockNum(int num)
{
	this->inodeBlockNum = num;
	return 1;
}
/*��ȡinode��ռ�õ��̿���Ŀ*/
int superblock::getInodeBlockNum()
{
	return this->inodeBlockNum;
}
/*���õ�ǰϵͳ�е��û���*/
int superblock::setUserNum(int num)
{
	this->userNum = num;
	return 1;
}
/*��ȡ��ǰϵͳ�д��ڵ��û���*/
int superblock::getUserNum()
{
	return this->userNum;
}
/*����Ŀ¼�ļ���inode��id��*/
int superblock::setDirectoryInodeId(int num)
{
	this->directoryInodeId = num;
	return 1;
}
/*��ȡĿ¼�ļ���inode��id��*/
int superblock::getDirectoryInodeId()
{
	return this->directoryInodeId;
}
//��ʼ�����п��λʾͼ
void superblock::initFreeBlockBitMap()    
{
	for(int i =0 ;i<BLOCK_NUM/32;i++)
		this->freeblockbitmap[i] = (unsigned int)0;
}
//��ʼ�����нڵ��λʾͼ
void superblock::initFreeInodeBitMap()
{
	for(int i =0 ;i<BLOCK_NUM/32;i++)
		this->freeinodebitmap[i] = (unsigned int)0;
}
void superblock::setFreeBlockBitMap()   //
{

}
unsigned int * superblock::getFreeBlockBitMap()    //��ȡ���п�λʾͼ����
{
	return this->freeblockbitmap;
}
void superblock::setFreeInodeBitMap()
{
	
}
unsigned int * superblock::getFreeInodeBitMap()    //��ȡ���нڵ�λʾͼ����
{
	return this->freeinodebitmap;
}
/*����û�*/
int superblock::addUser(user &u)
{
	//cout<<this->userNum<<endl;
	if(this->userNum < MAX_USER_SIZE)    //����û�����û�г�������û���
	{
		this->users[this->userNum].setName(u.getName());
		this->users[this->userNum].setPass(u.getPass());
		this->userNum = this->userNum+1;
		return 1;
	}
	else
	{
		cout<<"���ɴ��������û���"<<endl;
		return 0;
	}
}
/*��ȡ���е��û�*/
user * superblock::getUser()
{
	return this->users;
}
/*�任int���͵�����ĳλ������λ    ���ԭ����0 ���1 ���ԭ����1 ���0 */
void superblock::changeBit(unsigned int*arr,int index)    //�����ĺ��壬Ҫ���ĵĿ��   ���Ǵ�0��ʼ������ index��0��ʼ����
{
	//����������unsigned int ���ͣ�ռ�ĸ��ֽڣ�Ҳ����˵�ܹ���32λ�����������е�ÿ��Ԫ�ؿ���32�����ݿ�

	int remainder = index % 32 ;    //����  λ�ƣ�Ҫ�޸ĵ�λ��
	int shang = index / 32 ;     //��   �ڼ�������Ԫ��
	unsigned int temp = arr[shang];

	//��0��ʼ����λ����
	temp^=(1<<remainder) ;   //    ת��   ��Ϊ��0��ʼ����λ����   temp^=(1<<remainder-1) ; ������Ǵ�1��ʼ������
	arr[shang] = temp ;
}
/*���ж�ʮ������ d �Ķ����Ƶ�nλ�Ƿ���1*/
bool superblock::isOne(unsigned int *arr,int index)    //�±��1��ʼ����ס����0
{
	/*
		������ж�ʮ������ d �Ķ����Ƶ�nλ�Ƿ���1��Ӧ�������µ���m������&����
		m = 1 << n-1 ��n�Ǵ���0������)
		���Ϲ�ʽ
		n=1��ʱ��m=1
		n=2��ʱ��m=2
		n=3��ʱ��m=4
		n=4��ʱ��m=8
		...
		����10 & 2��������n=2�������
	*/

	int remainder = index % 32 ;    //����  λ�ƣ�Ҫ�޸ĵ�λ��
	int shang = index / 32 ;     //��   �ڼ�������Ԫ��
	unsigned int temp = arr[shang];

	int m = 1 << remainder;
	int result = temp & m;
	if(result == m)
		return true;
	else
		return false;
}


//������inode�ĳ�Ա����
inode::inode()
{
	for(int i=0;i<DIRECT_BLOCK_SIZE;i++)     //��ʼ����ַ�ռ�
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
void inode::addAddr(int num)    //����ļ� ���̿�
{
	if(this->blockNum<DIRECT_BLOCK_SIZE)
	{
		this->addr[this->blockNum] = num;
		this->blockNum = this->blockNum+1;     //�̿��¼����1
	}else    //�����ʱ����ļ������ĵĴ��̿��Ѿ�����ֱ���̿������ռ���
		//��ô���ö�������
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

///FCB   ��������
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

