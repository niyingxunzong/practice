
//磁盘文件分块，每个块为1K = 1024B

#define DISK_SIZE 1024*1024    // 磁盘大小   1M   单位字节
#define BLOCK_SIZE 1024    //划分磁盘块的大小1k
#define BLOCK_NUM DISK_SIZE / BLOCK_SIZE    //划分的磁盘块数
#define FILE_NAME_SIZE 10    //文件名字限定10个字节
#define USER_NAME_SIZE 10    //用户名长度
#define USER_PASS_SIZE 10    //用户密码长度
#define DIRECT_BLOCK_SIZE 5    //inode中的直接盘块数
#define DATE_TIME_SIZE 10    //日期时间字符串长度  2013-11-12
#define MAX_USER_SIZE 5    //限定最大的用户数    
#define MAX_CHILDREN_NUM 10    //最大子文件夹数
#define MAX_INODE_NUM BLOCK_NUM    //最大的inode数目  inode最多需要和磁盘块数相同，即每个文件占用一个磁盘块，但这是不可能的，因为
								   ///超级块占用0号磁盘块，他不需要inode，存放inode本身也要占用磁盘块
#define SINGLE_INODE_SIZE sizeof(inode)    //单个inode节点的大小
//#define MAX_INODE_BLOCK_NUM ((MAX_INODE_NUM * SINGLE_INODE_SIZE)/BLOCK_SIZE)//60//(MAX_INODE_NUM * SINGLE_INODE_SIZE)/BLOCK_SIZE    //存放inode所需要的block块数  
								//这个需要计算  存放inode所需要的block块数 = (inode数 * 每个inode大小)/每个磁盘块大小



//用户类
class user
{
public :
	user();
	user(char*,char*);   //构造函数
	void setName(char *);
	char * getName();
	void setPass(char *);
	char * getPass();
private:
	char user_name[USER_NAME_SIZE];    // 用户名
	char pass_word[USER_PASS_SIZE];    // 密码
};

//超级块类
class superblock   //超级块
{
public :
	superblock();
	int setBlockNum(int num);    //设置盘块总数
	int getBlockNum();    //获取盘块总数

	int setFreeBlockNum(int num);    //设置空闲块的块数
	int getFreeBlockNum();    //获取空闲块的总数

	int setInodeNum(int num);    //设置i节点的个数
	int getInodeNum();    //获取i节点的zong数

	int setFreeInodeNum(int num);    //设置空闲i节点的个数
	int getFreeInodeNum();    //获取空闲i节点的个数

	int setInodeStartBlockNum(int num);    //设置存放inode的块的起始号
	int getInodeStartBlockNum();    //获取存放inode的块的起始号

	int setInodeBlockNum(int num);    //设置放置inode的块的 总共用的块的数目
	int getInodeBlockNum();    //

	int setDirectoryInodeId(int inodeId);    //设置目录文件所对应的inode的id号
	int getDirectoryInodeId();    //获取目录文件的inode的id号

	int setUserNum(int num);    //设置当前系统中存在的用户数
	int getUserNum();    //获取当前系统中的用户的数量

	int addUser(user &u);     //添加用户
	user* getUser();    //所有用户

	void initFreeBlockBitMap();    //初始化空闲块的位示图
	void initFreeInodeBitMap();    //初始化空闲节点的位示图

	void setFreeBlockBitMap();    //
	unsigned int *getFreeBlockBitMap();    //获取空闲块位示图数组

	void setFreeInodeBitMap();
	unsigned int *getFreeInodeBitMap();    //获取空闲节点位示图数组

	void changeBit(unsigned int*arr,int index);    //变换int类型的数的某位二进制位    如果原来是0 变成1 如果原来是1 变成0 
	bool isOne(unsigned int*arr,int index);     //判断某个数的某位二进制是否为1
private :
	int blocknum ;    //盘块总数
	int freeblocknum ;    //空闲盘块总数
	int inodenum ;    // 节点总数
	int freeInodeNum ; // 空闲节点数
	//int inodeBlockStack[MAX_INODE_BLOCK_NUM];    //存放inode所需要的盘块数，这个数组存放这些盘块的盘块号
	int inodeStartBlockNum;    //存放inode的起始盘号
	int inodeBlockNum;    //inode所占用的盘块数

	int userNum ;    //当前已经存在的用户的数量
	int directoryInodeId;    //目录文件的inode id号

	unsigned int freeblockbitmap[BLOCK_NUM/32];     //unsigned int 占四个字节，也就是32位能存储  位示图法记录空闲块
	unsigned int freeinodebitmap[BLOCK_NUM/32];    //位示图法标志空闲节点 
	user users[MAX_USER_SIZE];    //存放用户信息的表的块号
};

//目录文件名和inode号的对应关系  类
class FCB   //目录文件名和inode号的对应关系
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
	char owener[USER_NAME_SIZE] ;    //文件所有者 comcons
	char filename[FILE_NAME_SIZE];   //文件名字
	int inodeId;
};

// inode节点类
class inode      //目前占60个字节
{
public :
	inode();
	void setId(int id);    //设置inode的id
	int getId();    //获取inode的id

	void setFileSize(int size);    //设置inode所对应的文件的大小
	int getFileSize();    //获取文件大小

	void setBlockNum(int num);    //设置文件所有的盘块数
	int getBlockNum();    //获取文件所对应的盘块数

	void initAddr();    //初始化地址空间   全部初始化为-1
	int * getAddr();    //获得地址空间 数组
	void addAddr(int num);    //添加磁盘块地址

	void setUser(char *);    //设置用户名
	char * getUser();    //获取该文件的用户名

	void setChangeTime(char * );    //设置最近修改时间
	char * getChangeTieme();    //获取最近的修改时间
private:
	int id;    //inode 的编号
	int fileSize ;    //文件大小
	int blockNum ;    //文件盘块数
	int addr[DIRECT_BLOCK_SIZE] ;   //5个直接盘块号
	int addr_index1;    //一个一次间接地址
	int addr_index2;    //一个两级间接地址

	char owener[USER_NAME_SIZE] ;    //文件所有者 comcons
	char changeTime[DATE_TIME_SIZE];    //最近的修改时间   2014-01-07
};

//目录类
class directory
{
public :
	directory();
private:
	int id ;    //目录文件号  唯一id
	int parentId ;    //父目录的id
	char * parentNode[FILE_NAME_SIZE];   //父目录名
	FCB fcb[MAX_CHILDREN_NUM];    ////存放所有的子节点的id 和子节点的名字对应关系
};



