
//以下是操作函数

/*初始化磁盘文件*/
int initDisk(superblock &sb);

/*磁盘格式化*/
int formatDisk(superblock &sb);

/*创建用户*/
int createAccount(char * username ,char * password,superblock &sup);



/*用户登录*/
int userLogin(superblock &sb);

/*文件操作选项*/
void fileOpation();
/*文件创建*/
int createFile(superblock &sb,char * filename);
/*文件打开*/
int openFile(superblock &sb,char * filename);
/*文件写入*/
int writeFile(superblock &sb,char * filename);


///读取节点
inode * readInode(int inodeId);

/*写入FCB*/
int writeToFCB(char * filename ,char * owener,int inodeId);