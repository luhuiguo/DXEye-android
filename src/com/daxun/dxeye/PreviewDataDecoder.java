package com.daxun.dxeye;

import static com.daxun.dxeye.Constants.CMD_PREVIEW_DATA;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by luhuiguo on 13-7-7.
 */
public class PreviewDataDecoder extends SNVRMessageDecoder {

	private static final Logger logger = LoggerFactory
			.getLogger(PreviewResponseDecoder.class);

	public PreviewDataDecoder() {
		super(CMD_PREVIEW_DATA);
	}

	@Override
	protected SNVRMessage decodeBody(IoSession session, IoBuffer in)
			throws Exception {

		//logger.error("decodeBody session:{} in:{}", session, in);
		PreviewData m = new PreviewData();

		m.setType(in.getInt());
		m.setSize(in.getInt());

		Payload p = new Payload();
		in.order(ByteOrder.LITTLE_ENDIAN);
		p.setSize(in.getInt());

		p.setMagic1(in.getInt());

		p.setMagic2(in.getInt());
		p.setLength(in.getInt());
		p.setWidth(in.getInt());
		p.setHeight(in.getInt());
		p.setStreamType(in.get());
		p.setSubStreamType(in.get());
		p.setFrameType(in.get());

		in.get(); // reserved
		in.get(); // reserved1
		in.get(); // reserved1

		p.setAudioChannelCount(in.get());
		p.setAudioBits(in.get());
		p.setTimeStamp1(in.getInt());
		p.setTimeStamp2(in.getInt());
		p.setAudioSamples(in.getInt());

		in.getInt(); // reserved2

		p.setLineCount(in.get());
		p.setLineWidth(in.get());

		// reserved3
		for (int i = 0; i < 14; i++) {
			in.get();
		}
		// frame data
		byte[] data = new byte[p.getLength()];
		in.get(data);
		p.setData(data);

		// OSD

		if (p.getLineCount() > 0 && p.getLineWidth() > 0) {

			List<Line> lines = new ArrayList<Line>();
			for (int i = 0; i < p.getLineCount(); i++) {
				Line line = new Line();

				line.setX(in.getShort());
				line.setY(in.getShort());

				//logger.error("OSD {} {}", in.position(), in.limit());
				byte[] b = new byte[p.getLineWidth() - 4];

				in.get(b);
				try {
					int pos = ArrayUtils.indexOf(b, (byte)0);
					String str;
					if (pos >0) {
						str = new String(b,0,pos,"UTF8");
					}else{
						str = new String(b,"UTF8");
					}
					line.setContent(str);
				} catch (Exception e) {
					logger.error("Exception", e);
				}

				lines.add(line);

			}

			p.setLines(lines);

		}

		m.setPayload(p);
		in.order(ByteOrder.BIG_ENDIAN);

		return m;
	}

}