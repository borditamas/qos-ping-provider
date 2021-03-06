package ai.aitia.qosping.service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ai.aitia.qosping.service.publish.Publisher;
import ai.aitia.qosping.service.task.IcmpPingJob;
import ai.aitia.qosping.service.task.queue.IcmpPingJobQueue;
import eu.arrowhead.client.skeleton.provider.Constant;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.EventPublishRequestDTO;
import eu.arrowhead.common.dto.shared.IcmpPingRequestACK;
import eu.arrowhead.common.dto.shared.IcmpPingRequestDTO;
import eu.arrowhead.common.dto.shared.QosMonitorEventType;

@Service
public class IcmpPingService {
	
	//=================================================================================================
	// methods
	
	@Autowired
	private IcmpPingJobQueue jobQueue;
	
	@Autowired
	private Publisher publisher;
	
	private static final String ACK_MSG = "OK";
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public IcmpPingRequestACK enrollRequest(final IcmpPingRequestDTO request) {
		validateIcmpPingRequestDTO(request);
		
		final IcmpPingJob job = new IcmpPingJob(UUID.randomUUID(), request.getHost(), request.getTtl(), request.getPacketSize(), request.getTimeout(), request.getTimeToRepeat());
		publishReceived(job);
		jobQueue.put(job);		
		
		final IcmpPingRequestACK ack = new IcmpPingRequestACK();
		ack.setAckOk(ACK_MSG);
		ack.setExternalMeasurementUuid(job.getJobId());
		return ack;
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public void validateIcmpPingRequestDTO(final IcmpPingRequestDTO request) {
		Assert.notNull(request, "request is null");
		Assert.isTrue(!Utilities.isEmpty(request.getHost()), "request.host is empty");
		if (request.getTtl() != null) {
			Assert.isTrue(request.getTtl() > 0, "request.ttl is negative or zero");			
		}
		if (request.getPacketSize() != null) {
			Assert.isTrue(request.getPacketSize() > 0, "request.packetSize is negative or zero");			
		}
		if (request.getTimeout() != null) {
			Assert.isTrue(request.getTimeout() > 0, "request.timeout is negative or zero");			
		}
		Assert.notNull(request.getTimeToRepeat(), "request.timeToRepeat is null");
		Assert.isTrue(request.getTimeToRepeat() > 0, "request.timeToRepeat is negative or zero");
	}
	
	//-------------------------------------------------------------------------------------------------
	private void publishReceived(final IcmpPingJob job) {
		final EventPublishRequestDTO event = new EventPublishRequestDTO();
		event.setEventType(QosMonitorEventType.RECEIVED_MONITORING_REQUEST.name());
		event.setMetaData(Map.of(Constant.PROCESS_ID, job.getJobId().toString()));
		event.setPayload(Constant.EMPTY_ARRAY_STR);
		event.setTimeStamp(Utilities.convertZonedDateTimeToUTCString(ZonedDateTime.now()));
		publisher.publish(event);
	}
}
