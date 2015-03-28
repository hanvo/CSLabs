/*  main.c  - main */
//this test case is revised from weichu's


#include <xinu.h>
#include <stdio.h>

// blocking message send test
#define PRI 30

static int score = 8;       //basic

sid32 sema;
static int test0_success = 0;
static int test0_sender_returned = 0;

static void receiver0(void){
  sleep(1);
  if (proctab[currpid+1].prstate == PR_SND) {
        score+=6;
        kprintf("Send State Test: Pass\r\n");
    } else {
        kprintf("Send State Test: fail: didn't define PR_SND properly\r\n");
    }
  receiveb();
  kprintf("Test0: call receive()\r\n");
  if( test0_sender_returned ){
    test0_success = 1;
  }else{
    kprintf("Test0: fail: timeout, the first sendb() should return immediately.\r\n");
  }
  uint32 msg = receiveb();
  kprintf("Test0: receive msg: %d\r\n", msg);
  if( test0_success ){
    score+= 6;
    kprintf("Test0: Pass\r\n");
  }
  kprintf("Exit\r\n");
}

static void sender0(int pid){
  sendb( pid, 1 ); //shouldn't block
  test0_sender_returned = 1;
  sendb( pid, 2 ); //should block
  kprintf("Test0: sender returned\r\n");
  //signal( sema0 );
}

#define TEST1_MESSAGE 5566
static void _receiver1(void){
  uint32 msg = receiveb();
  if( msg == TEST1_MESSAGE ){
    kprintf("Test1: Pass\r\n");
    score+=6;
  }else{
    kprintf("Test1: fail: received incorrect value from the sender. received %d, expect %d\r\n", msg, TEST1_MESSAGE);
  }
  signal( sema );
}

static void _sender1(int pid){
  sendb( pid, TEST1_MESSAGE );
}

#define TEST2_OFFSET 1024
static void _receiver2(void){
  int n;
  for(n = 0; n< 100; n++ ){
    uint32 msg = receiveb();
    if( msg != n+TEST2_OFFSET ){
      kprintf("Test2: fail: did not receive in sending order at %d th message.\r\n", n);
      signal( sema );
      return;
    }
  }
  kprintf("Test2: pass\r\n");
  score+=6;
  signal( sema );
}

static void _sender2(int pid){
  int n;
  for(n = 0; n< 100; n++ ){
    sendb( pid, n+TEST2_OFFSET );
  }
}

static int test3_result = 0;
static void _receiver3(void){
  int n;
  for(n = 0; n< 5; n++ ){
    uint32 msg = 0;
	msg=receiveb();
    test3_result++;
  }
}

static void _sender3(int pid){
  sendb( pid, 1 );
}

#define TEST4_OFFSET 765
static void receiver4(void){
  int n;
  sleep(6); // sleep several seconds to force all senders to wait
  for(n = 0; n< 5; n++ ){
    uint32 msg = receiveb();
    if( msg != n+TEST4_OFFSET ){
      kprintf("Test4: fail: did not receive in sending order at %d th sender\r\n", n);
      signal( sema );
      return;
    }
  }
  kprintf("Test4: pass\r\n");
  score+=6;
  signal( sema );
}

static void sender4(int pid, int n){
  sendb( pid, n+TEST4_OFFSET );
}


// grading criteria:
// programming 100 points
//
// (1) every sent messages must arrive
// (2) receiving order conforms sending order
//
// basic: 8 pts     successfully compile
// sendb/receiveb: 66 pts, 6 pts for each test case
// senda: 26 = 6 + 10 + 10
//

void test0(void){
  kprintf("Test 0 -- One sender one receiver. the sender should return immediately. \r\n");

  //sema0 = semcreate( 0 );
  int pid = create(receiver0, 1024, PRI, "receiver0", 0);
  resume(pid);
  int senderpid = create(sender0, 1024, PRI, "sender0", 1, pid);
  resume(senderpid);
  sleep(3);
  if( kill( pid ) == OK ){
    kprintf("test 0 failed: receiver did not return\r\n");
  }

  kill( senderpid );
  kprintf("Test0 finished\r\n");
  sleep(1);
}

void test1(void){
  kprintf("Test 1 -- One sender one receiver. one message per sender. verify receiver gets the right message. \r\n");
  int pid = create(_receiver1, 1024, PRI, "receiver1", 0);
  resume(pid);
  
  resume(create(_sender1, 1024, PRI, "sender1", 1, pid));

  wait( sema );
  kprintf("Test 1 finished\r\n");
  sleep(1);
}

void test2(void){
  kprintf("Test 2 -- One sender one receiver. multiple messages per sender. \r\n");
  int pid = create(_receiver2, 1024, PRI, "receiver2", 0);
  resume(pid);
  
  resume(create(_sender2, 1024, PRI, "sender2", 1, pid));

  wait( sema );
  kprintf("Test 2 finished\r\n");
  sleep(1);
}

void test3(void){
  kprintf("Test 3 -- Multi-sender one receiver. one message per sender.  \r\n");
  int pid = create(_receiver3, 1024, PRI, "receiver3", 0);
  resume(pid);
  
  int n;
  for( n= 0; n< 5;n++){
    resume(create(_sender3, 1024, PRI, "sender3", 1, pid));
  }

  sleep(3);
  if( test3_result == 5 ){
    kprintf("Test3: pass\r\n");
    score+=6;
  }else{
    kprintf("Test 3: fail: timeout waiting for messages\r\n");
  }
  kprintf("Test 3 finished\r\n");
  sleep(1);
}

void test4(void){
  kprintf("Test 4 -- Multi-sender one receiver. one message per sender . is receiving order the same as sending order? \r\n");
  int pid = create(receiver4, 1024, PRI, "receiver4", 0);
  resume(pid);
  
  int n;
  int senderpid[5];
  for( n= 0; n< 5;n++){
    senderpid[ n ] = create(sender4, 1024, PRI, "sender4", 2, pid, n);
    resume(senderpid[ n ] );
    sleep(1); // sleep 1 second to let the previous sender block sending.
  }
  wait( sema );
  sleep(1); // sleep 1 second to let all processes finish
  for(n=0;n<5;n++){
    kill( senderpid[n] );
  }
  kprintf("Test 4 finished\r\n");
}

void blocksending_test(void) {

  sema = semcreate(0);

  kprintf("======================Start Testing=====================\r\n");
  test0();

  //test1();

  //test2();

  //test3();

  //test4();
  
  kprintf("Total Score: %d\r\n", score );
  kprintf("======================End of Test=====================\r\n");
}

/*
// Asynchronous message receive test
umsg32 recvbuf;
int myrecvhandler(void) {
    kprintf("msg received = %d\r\n", recvbuf);
    return(OK);
}

void test_sender01(pid32 pid, umsg32 msg) {
	senda(pid, msg);
}

void test_receiver(void) {
    if (registercb(&recvbuf, myrecvhandler) != OK) {
        kprintf("recv handler registration failed\r\n");
        return ;
    }
	while (1)
		;
}

void areceive_test01(void) {
	pid32 pid;
	pid=create(test_receiver, 1024, 20, "receive", 0);
	resume(pid);
	resume(create(test_sender01, 1024, 20, "senda", 2, pid, 10));
	sleep(5);
	kill(pid);
}

void areceive_test(void) {
    kprintf("======================Start Testing=====================\r\n");
	kprintf("Test1:\r\n");
	areceive_test01();	//1 send, 1 receiver
    kprintf("======================End of Test=====================\r\n");
}
*/

int main(int argc, char **argv) {
  kprintf("Start Testing\r\n");
	blocksending_test();
	//areceive_test();
  kprintf("End\r\n");
	return OK;
}


