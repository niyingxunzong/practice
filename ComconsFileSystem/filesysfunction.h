
//�����ǲ�������

/*��ʼ�������ļ�*/
int initDisk(superblock &sb);

/*���̸�ʽ��*/
int formatDisk(superblock &sb);

/*�����û�*/
int createAccount(char * username ,char * password,superblock &sup);



/*�û���¼*/
int userLogin(superblock &sb);

/*�ļ�����ѡ��*/
void fileOpation();
/*�ļ�����*/
int createFile(superblock &sb,char * filename);
/*�ļ���*/
int openFile(superblock &sb,char * filename);
/*�ļ�д��*/
int writeFile(superblock &sb,char * filename);


///��ȡ�ڵ�
inode * readInode(int inodeId);

/*д��FCB*/
int writeToFCB(char * filename ,char * owener,int inodeId);