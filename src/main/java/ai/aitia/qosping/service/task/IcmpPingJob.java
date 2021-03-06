package ai.aitia.qosping.service.task;

import java.util.UUID;

import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;

public class IcmpPingJob {
	
	//=================================================================================================
	// members

	private final  UUID jobId;
	private final String host;
	private final Integer ttl;
	private final Integer packetSize;
	private final Long timeout;
	private final int timeToRepeat;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	public IcmpPingJob(final UUID jobId, final String host, final Integer ttl, final Integer packetSize, final Long timeout, final int timeToRepeat) {
		Assert.notNull(jobId, "jobId is null.");
		Assert.isTrue(!Utilities.isEmpty(host), "host is empty");
		
		this.jobId = jobId;
		this.host = host;
		this.ttl = ttl;
		this.packetSize = packetSize;
		this.timeout = timeout;
		this.timeToRepeat = timeToRepeat;
	}

	//-------------------------------------------------------------------------------------------------	
	public UUID getJobId() { return jobId; }
	public String getHost() { return host; }
	public Integer getTtl() { return ttl; }
	public Integer getPacketSize() { return packetSize; }
	public Long getTimeout() { return timeout; }
	public int getTimeToRepeat() { return timeToRepeat; }
}
