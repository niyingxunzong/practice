
//�����ļ��ֿ飬ÿ����Ϊ1K = 1024B

#define DISK_SIZE 1024*1024    // ���̴�С   1M   ��λ�ֽ�
#define BLOCK_SIZE 1024    //���ִ��̿�Ĵ�С1k
#define BLOCK_NUM DISK_SIZE / BLOCK_SIZE    //���ֵĴ��̿���
#define FILE_NAME_SIZE 10    //�ļ������޶�10���ֽ�
#define USER_NAME_SIZE 10    //�û�������
#define USER_PASS_SIZE 10    //�û����볤��
#define DIRECT_BLOCK_SIZE 5    //inode�е�ֱ���̿���
#define DATE_TIME_SIZE 10    //����ʱ���ַ�������  2013-11-12
#define MAX_USER_SIZE 5    //�޶������û���    
#define MAX_CHILDREN_NUM 10    //������ļ�����
#define MAX_INODE_NUM BLOCK_NUM    //����inode��Ŀ  inode�����Ҫ�ʹ��̿�����ͬ����ÿ���ļ�ռ��һ�����̿飬�����ǲ����ܵģ���Ϊ
								   ///������ռ��0�Ŵ��̿飬������Ҫinode�����inode����ҲҪռ�ô��̿�
#define SINGLE_INODE_SIZE sizeof(inode)    //����inode�ڵ�Ĵ�С
//#define MAX_INODE_BLOCK_NUM ((MAX_INODE_NUM * SINGLE_INODE_SIZE)/BLOCK_SIZE)//60//(MAX_INODE_NUM * SINGLE_INODE_SIZE)/BLOCK_SIZE    //���inode����Ҫ��block����  
								//�����Ҫ����  ���inode����Ҫ��block���� = (inode�� * ÿ��inode��С)/ÿ�����̿��С



//�û���
class user
{
public :
	user();
	user(char*,char*);   //���캯��
	void setName(char *);
	char * getName();
	void setPass(char *);
	char * getPass();
private:
	char user_name[USER_NAME_SIZE];    // �û���
	char pass_word[USER_PASS_SIZE];    // ����
};

//��������
class superblock   //������
{
public :
	superblock();
	int setBlockNum(int num);    //�����̿�����
	int getBlockNum();    //��ȡ�̿�����

	int setFreeBlockNum(int num);    //���ÿ��п�Ŀ���
	int getFreeBlockNum();    //��ȡ���п������

	int setInodeNum(int num);    //����i�ڵ�ĸ���
	int getInodeNum();    //��ȡi�ڵ��zong��

	int setFreeInodeNum(int num);    //���ÿ���i�ڵ�ĸ���
	int getFreeInodeNum();    //��ȡ����i�ڵ�ĸ���

	int setInodeStartBlockNum(int num);    //���ô��inode�Ŀ����ʼ��
	int getInodeStartBlockNum();    //��ȡ���inode�Ŀ����ʼ��

	int setInodeBlockNum(int num);    //���÷���inode�Ŀ�� �ܹ��õĿ����Ŀ
	int getInodeBlockNum();    //

	int setDirectoryInodeId(int inodeId);    //����Ŀ¼�ļ�����Ӧ��inode��id��
	int getDirectoryInodeId();    //��ȡĿ¼�ļ���inode��id��

	int setUserNum(int num);    //���õ�ǰϵͳ�д��ڵ��û���
	int getUserNum();    //��ȡ��ǰϵͳ�е��û�������

	int addUser(user &u);     //����û�
	user* getUser();    //�����û�

	void initFreeBlockBitMap();    //��ʼ�����п��λʾͼ
	void initFreeInodeBitMap();    //��ʼ�����нڵ��λʾͼ

	void setFreeBlockBitMap();    //
	unsigned int *getFreeBlockBitMap();    //��ȡ���п�λʾͼ����

	void setFreeInodeBitMap();
	unsigned int *getFreeInodeBitMap();    //��ȡ���нڵ�λʾͼ����

	void changeBit(unsigned int*arr,int index);    //�任int���͵�����ĳλ������λ    ���ԭ����0 ���1 ���ԭ����1 ���0 
	bool isOne(unsigned int*arr,int index);     //�ж�ĳ������ĳλ�������Ƿ�Ϊ1
private :
	int blocknum ;    //�̿�����
	int freeblocknum ;    //�����̿�����
	int inodenum ;    // �ڵ�����
	int freeInodeNum ; // ���нڵ���
	//int inodeBlockStack[MAX_INODE_BLOCK_NUM];    //���inode����Ҫ���̿����������������Щ�̿���̿��
	int inodeStartBlockNum;    //���inode����ʼ�̺�
	int inodeBlockNum;    //inode��ռ�õ��̿���

	int userNum ;    //��ǰ�Ѿ����ڵ��û�������
	int directoryInodeId;    //Ŀ¼�ļ���inode id��

	unsigned int freeblockbitmap[BLOCK_NUM/32];     //unsigned int ռ�ĸ��ֽڣ�Ҳ����32λ�ܴ洢  λʾͼ����¼���п�
	unsigned int freeinodebitmap[BLOCK_NUM/32];    //λʾͼ����־���нڵ� 
	user users[MAX_USER_SIZE];    //����û���Ϣ�ı�Ŀ��
};

//Ŀ¼�ļ�����inode�ŵĶ�Ӧ��ϵ  ��
class FCB   //Ŀ¼�ļ�����inode�ŵĶ�Ӧ��ϵ
{
public :
	FCB();
	void setFileName(char *);
	char * getFileName();

	void setOwener(char *);
	char * getOwener();

	void setInodeId(int);
	int getInodeId();
private:
	char owener[USER_NAME_SIZE] ;    //�ļ������� comcons
	char filename[FILE_NAME_SIZE];   //�ļ�����
	int inodeId;
};

// inode�ڵ���
class inode      //Ŀǰռ60���ֽ�
{
public :
	inode();
	void setId(int id);    //����inode��id
	int getId();    //��ȡinode��id

	void setFileSize(int size);    //����inode����Ӧ���ļ��Ĵ�С
	int getFileSize();    //��ȡ�ļ���С

	void setBlockNum(int num);    //�����ļ����е��̿���
	int getBlockNum();    //��ȡ�ļ�����Ӧ���̿���

	void initAddr();    //��ʼ����ַ�ռ�   ȫ����ʼ��Ϊ-1
	int * getAddr();    //��õ�ַ�ռ� ����
	void addAddr(int num);    //��Ӵ��̿��ַ

	void setUser(char *);    //�����û���
	char * getUser();    //��ȡ���ļ����û���

	void setChangeTime(char * );    //��������޸�ʱ��
	char * getChangeTieme();    //��ȡ������޸�ʱ��
private:
	int id;    //inode �ı��
	int fileSize ;    //�ļ���С
	int blockNum ;    //�ļ��̿���
	int addr[DIRECT_BLOCK_SIZE] ;   //5��ֱ���̿��
	int addr_index1;    //һ��һ�μ�ӵ�ַ
	int addr_index2;    //һ��������ӵ�ַ

	char owener[USER_NAME_SIZE] ;    //�ļ������� comcons
	char changeTime[DATE_TIME_SIZE];    //������޸�ʱ��   2014-01-07
};

//Ŀ¼��
class directory
{
public :
	directory();
private:
	int id ;    //Ŀ¼�ļ���  Ψһid
	int parentId ;    //��Ŀ¼��id
	char * parentNode[FILE_NAME_SIZE];   //��Ŀ¼��
	FCB fcb[MAX_CHILDREN_NUM];    ////������е��ӽڵ��id ���ӽڵ�����ֶ�Ӧ��ϵ
};



