/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tobikster.streamingtester.utils;

import java.util.Locale;

/**
 * Holds statically defined sample definitions.
 */
public class Samples {

	public static final int TYPE_DASH = 0;
	public static final int TYPE_SS = 1;
	public static final int TYPE_HLS = 2;
	public static final int TYPE_OTHER = 3;

	// @formatter:off
	public static final Sample[] DASH = new Sample[] {
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_1200_1280x720_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_1200_1280x720_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_1200_1280x720_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_1200_1280x720_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_1200_1280x720_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_1200_1280x720_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_1200_1366x768_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_1200_1366x768_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_1200_1366x768_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_1200_1366x768_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_1200_1366x768_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_1200_1366x768_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_1200_1920x1080_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_1200_1920x1080_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_1200_1920x1080_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_1200_1920x1080_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_1200_1920x1080_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_1200_1920x1080_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_2400_1280x720_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_2400_1280x720_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_2400_1280x720_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_2400_1280x720_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_2400_1280x720_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_2400_1280x720_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_2400_1366x768_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_2400_1366x768_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_2400_1366x768_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_2400_1366x768_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_2400_1366x768_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_2400_1366x768_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_2400_1920x1080_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_2400_1920x1080_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_2400_1920x1080_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_2400_1920x1080_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_2400_1920x1080_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_2400_1920x1080_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_4800_1280x720_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_4800_1280x720_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_4800_1280x720_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_4800_1280x720_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_4800_1280x720_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_4800_1280x720_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_4800_1366x768_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_4800_1366x768_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_4800_1366x768_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_4800_1366x768_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_4800_1366x768_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_4800_1366x768_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - h264 - seg 1", "dash/streaming_tester/video/sample_fps_24_br_4800_1920x1080_mp4_h264_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - h264 - seg 2", "dash/streaming_tester/video/sample_fps_24_br_4800_1920x1080_mp4_h264_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - h264 - seg 4", "dash/streaming_tester/video/sample_fps_24_br_4800_1920x1080_mp4_h264_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - xvid - seg 1", "dash/streaming_tester/video/sample_fps_24_br_4800_1920x1080_mp4_xvid_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - xvid - seg 2", "dash/streaming_tester/video/sample_fps_24_br_4800_1920x1080_mp4_xvid_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - xvid - seg 4", "dash/streaming_tester/video/sample_fps_24_br_4800_1920x1080_mp4_xvid_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 128 - sr 22050 - mp4 - libmp3lame - seg 1", "dash/streaming_tester/audio/sample_sr_22050_br_128_mp4_libmp3lame_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 128 - sr 22050 - mp4 - libmp3lame - seg 2", "dash/streaming_tester/audio/sample_sr_22050_br_128_mp4_libmp3lame_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 128 - sr 22050 - mp4 - libmp3lame - seg 4", "dash/streaming_tester/audio/sample_sr_22050_br_128_mp4_libmp3lame_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 64 - sr 22050 - mp4 - libmp3lame - seg 1", "dash/streaming_tester/audio/sample_sr_22050_br_64_mp4_libmp3lame_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 64 - sr 22050 - mp4 - libmp3lame - seg 2", "dash/streaming_tester/audio/sample_sr_22050_br_64_mp4_libmp3lame_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 64 - sr 22050 - mp4 - libmp3lame - seg 4", "dash/streaming_tester/audio/sample_sr_22050_br_64_mp4_libmp3lame_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 128 - sr 44100 - mp4 - libmp3lame - seg 1", "dash/streaming_tester/audio/sample_sr_44100_br_128_mp4_libmp3lame_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 128 - sr 44100 - mp4 - libmp3lame - seg 2", "dash/streaming_tester/audio/sample_sr_44100_br_128_mp4_libmp3lame_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 128 - sr 44100 - mp4 - libmp3lame - seg 4", "dash/streaming_tester/audio/sample_sr_44100_br_128_mp4_libmp3lame_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 64 - sr 44100 - mp4 - libmp3lame - seg 1", "dash/streaming_tester/audio/sample_sr_44100_br_64_mp4_libmp3lame_seg_dash_1/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 64 - sr 44100 - mp4 - libmp3lame - seg 2", "dash/streaming_tester/audio/sample_sr_44100_br_64_mp4_libmp3lame_seg_dash_2/stream.mpd", TYPE_DASH),
		new Sample("Audio - br 64 - sr 44100 - mp4 - libmp3lame - seg 4", "dash/streaming_tester/audio/sample_sr_44100_br_64_mp4_libmp3lame_seg_dash_4/stream.mpd", TYPE_DASH),
		new Sample("BigBuckBunny - test", "dash/streaming_tester/bunny/stream.mpd", TYPE_DASH)
	};
	// @formatter:on

	// @formatter:off
	public static final Sample[] HLS = new Sample[]{
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_1200_1280x720_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_1200_1280x720_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_1200_1280x720_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_1200_1280x720_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_1200_1280x720_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_1200_1280x720_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_1200_1366x768_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_1200_1366x768_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_1200_1366x768_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_1200_1366x768_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_1200_1366x768_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_1200_1366x768_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_1200_1920x1080_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_1200_1920x1080_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_1200_1920x1080_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_1200_1920x1080_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_1200_1920x1080_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_1200_1920x1080_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_2400_1280x720_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_2400_1280x720_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_2400_1280x720_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_2400_1280x720_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_2400_1280x720_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_2400_1280x720_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_2400_1366x768_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_2400_1366x768_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_2400_1366x768_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_2400_1366x768_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_2400_1366x768_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_2400_1366x768_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_2400_1920x1080_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_2400_1920x1080_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_2400_1920x1080_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_2400_1920x1080_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_2400_1920x1080_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_2400_1920x1080_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_4800_1280x720_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_4800_1280x720_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_4800_1280x720_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_4800_1280x720_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_4800_1280x720_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_4800_1280x720_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_4800_1366x768_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_4800_1366x768_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_4800_1366x768_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_4800_1366x768_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_4800_1366x768_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_4800_1366x768_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - h264 - seg 1", "hls/streaming_tester/video/sample_br_4800_1920x1080_mp4_h264_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - h264 - seg 2", "hls/streaming_tester/video/sample_br_4800_1920x1080_mp4_h264_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - h264 - seg 4", "hls/streaming_tester/video/sample_br_4800_1920x1080_mp4_h264_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - xvid - seg 1", "hls/streaming_tester/video/sample_br_4800_1920x1080_mp4_xvid_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - xvid - seg 2", "hls/streaming_tester/video/sample_br_4800_1920x1080_mp4_xvid_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - xvid - seg 4", "hls/streaming_tester/video/sample_br_4800_1920x1080_mp4_xvid_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 128 - sr 22050 - mp4 - libmp3lame - seg 1", "hls/streaming_tester/audio/sample_sr_22050_br_128_mp4_libmp3lame_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 128 - sr 22050 - mp4 - libmp3lame - seg 2", "hls/streaming_tester/audio/sample_sr_22050_br_128_mp4_libmp3lame_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 128 - sr 22050 - mp4 - libmp3lame - seg 4", "hls/streaming_tester/audio/sample_sr_22050_br_128_mp4_libmp3lame_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 64 - sr 22050 - mp4 - libmp3lame - seg 1", "hls/streaming_tester/audio/sample_sr_22050_br_64_mp4_libmp3lame_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 64 - sr 22050 - mp4 - libmp3lame - seg 2", "hls/streaming_tester/audio/sample_sr_22050_br_64_mp4_libmp3lame_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 64 - sr 22050 - mp4 - libmp3lame - seg 4", "hls/streaming_tester/audio/sample_sr_22050_br_64_mp4_libmp3lame_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 128 - sr 44100 - mp4 - libmp3lame - seg 1", "hls/streaming_tester/audio/sample_sr_44100_br_128_mp4_libmp3lame_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 128 - sr 44100 - mp4 - libmp3lame - seg 2", "hls/streaming_tester/audio/sample_sr_44100_br_128_mp4_libmp3lame_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 128 - sr 44100 - mp4 - libmp3lame - seg 4", "hls/streaming_tester/audio/sample_sr_44100_br_128_mp4_libmp3lame_seg_hls_4/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 64 - sr 44100 - mp4 - libmp3lame - seg 1", "hls/streaming_tester/audio/sample_sr_44100_br_64_mp4_libmp3lame_seg_hls_1/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 64 - sr 44100 - mp4 - libmp3lame - seg 2", "hls/streaming_tester/audio/sample_sr_44100_br_64_mp4_libmp3lame_seg_hls_2/stream.m3u8", TYPE_HLS),
		new Sample("Audio - br 64 - sr 44100 - mp4 - libmp3lame - seg 4", "hls/streaming_tester/audio/sample_sr_44100_br_64_mp4_libmp3lame_seg_hls_4/stream.m3u8", TYPE_HLS)
	};
	// @formatter:on

	// @formatter:off
	public static final Sample[] DIRECT_STREAMING = new Sample[] {
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_1200_1280x720_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 1200 - 1280x720 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_1200_1280x720_xvid.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_1200_1366x768_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 1200 - 1366x768 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_1200_1366x768_xvid.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_1200_1920x1080_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - vp8", "direct_streaming/streaming_tester/video/sample_fps_24_br_1200_1920x1080_vp8.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 1200 - 1920x1080 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_1200_1920x1080_xvid.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_2400_1280x720_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 2400 - 1280x720 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_2400_1280x720_xvid.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_2400_1366x768_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 2400 - 1366x768 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_2400_1366x768_xvid.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_2400_1920x1080_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 2400 - 1920x1080 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_2400_1920x1080_xvid.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_4800_1280x720_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 4800 - 1280x720 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_4800_1280x720_xvid.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_4800_1366x768_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 4800 - 1366x768 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_4800_1366x768_xvid.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - h264", "direct_streaming/streaming_tester/video/sample_fps_24_br_4800_1920x1080_h264.mp4", TYPE_OTHER),
		new Sample("Video - fps 24 - br 4800 - 1920x1080 - mp4 - xvid", "direct_streaming/streaming_tester/video/sample_fps_24_br_4800_1920x1080_xvid.mp4", TYPE_OTHER),
		new Sample("Audio - sr 22050 - br 128 - mp4 - aac", "direct_streaming/streaming_tester/audio/sample_sr_22050_br_128_aac.mp4", TYPE_OTHER),
		new Sample("Audio - sr 22050 - br 128 - mp4 - libmp3lame", "direct_streaming/streaming_tester/audio/sample_sr_22050_br_128_libmp3lame.mp4", TYPE_OTHER),
		new Sample("Audio - sr 22050 - br 64 - mp4 - aac", "direct_streaming/streaming_tester/audio/sample_sr_22050_br_64_aac.mp4", TYPE_OTHER),
		new Sample("Audio - sr 22050 - br 64 - mp4 - libmp3lame", "direct_streaming/streaming_tester/audio/sample_sr_22050_br_64_libmp3lame.mp4", TYPE_OTHER),
		new Sample("Audio - sr 44100 - br 128 - mp4 - aac", "direct_streaming/streaming_tester/audio/sample_sr_44100_br_128_aac.mp4", TYPE_OTHER),
		new Sample("Audio - sr 44100 - br 128 - mp4 - libmp3lame", "direct_streaming/streaming_tester/audio/sample_sr_44100_br_128_libmp3lame.mp4", TYPE_OTHER),
		new Sample("Audio - sr 44100 - br 64 - mp4 - aac", "direct_streaming/streaming_tester/audio/sample_sr_44100_br_64_aac.mp4", TYPE_OTHER),
		new Sample("Audio - sr 44100 - br 64 - mp4 - libmp3lame", "direct_streaming/streaming_tester/audio/sample_sr_44100_br_64_libmp3lame.mp4", TYPE_OTHER)
	};
	// @formatter:on

	private Samples() {
	}

	public static class Sample {

		public final String name;
		public final String contentId;
		public final String uri;
		public final int type;

		public Sample(String name, String uri, int type) {
			this(name, name.toLowerCase(Locale.US).replaceAll("\\s", ""), uri, type);
		}

		public Sample(String name, String contentId, String uri, int type) {
			this.name = name;
			this.contentId = contentId;
			this.uri = uri;
			this.type = type;
		}
	}

}
