/* receive.c - receive */

#include <xinu.h>


umsg32	receiveb(void)
{
	intmask	mask;			/* saved interrupt mask		*/
	struct	procent *prptr;		/* ptr to process' table entry	*/
	struct	procent *prblcked;		/* ptr to process' table entry	*/
	umsg32	msg;			/* message to return		*/
	pid32 pid;

	mask = disable();
	prptr = &proctab[currpid];
	if (prptr->prhasmsg == FALSE) {
		prptr->prstate = PR_RECV;
		resched();		/* block until message arrives	*/
	}
	msg = prptr->prmsg;			/* retrieve message		*/
	prptr->prhasmsg = FALSE;	/* reset message flag		*/
	/*
	* 	Check if you have something in the receiveq. 
	* 	if so you will dequeue recieveq 
	*	change that processes state to ready
	*/
	if(nonempty(prptr->receivelist))
	{
		pid = getfirst(prptr->receivelist);
		prblcked = &proctab[pid];
		prblcked->prstate = PR_READY;

	}

	restore(mask);
	return msg;
}
