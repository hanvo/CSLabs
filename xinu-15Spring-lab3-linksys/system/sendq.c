#include <xinu.h>


syscall	sendq(
	  pid32		pid,		/* ID of recipient process	*/
	  umsg32	msg		/* contents of message		*/
	)
{
	intmask	mask;			/* saved interrupt mask		*/
	struct	procent *prptr;		/* ptr to process' table entry	*/
	struct 	procent	*myself; 

	mask = disable();
	if (isbadpid(pid)) {
		restore(mask);
		return SYSERR;
	}
	
	myself = &proctab[currpid];
	prptr = &proctab[pid];

	if ((prptr->prstate == PR_FREE) || prptr->prhasmsg) {
		restore(mask);
		return SYSERR;
	}

	myself->prmsg = msg;
	//no more space in MSGQ(3) BLOCK 
	if(prptr->msgqCount > MSGQ_SIZE)
	{	
		myself->prstate = PR_SND;
		enqueue(currpid,prptr->receivelist);
	}
	else 
	{
		prptr->prhasmsg = TRUE;
		enqueue(currpid,prptr->msgq);
	}
	resched();


	/* If recipient waiting or in timed-wait make it ready */
	if (prptr->prstate == PR_RECV) {
		ready(pid, RESCHED_YES);
	} else if (prptr->prstate == PR_RECTIM) {
		unsleep(pid);
		ready(pid, RESCHED_YES);
	}
	restore(mask);		/* restore interrupts */
	return OK;
}
