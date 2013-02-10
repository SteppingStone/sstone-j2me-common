/*
 * Copyright (c) 2012 EDC
 * 
 * This file is part of Stepping Stone.
 * 
 * Stepping Stone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Stepping Stone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Stepping Stone.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */
package org.edc.sstone.j2me.audio;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

import org.edc.sstone.CheckedException;
import org.edc.sstone.Constants;
import org.edc.sstone.log.Log;
import org.edc.sstone.res.ResourceProvider;

/**
 * @author Greg Orlowski
 */
public class AudioPlayer {

    private Player player;
    private PlayerListener playerListener;
    private int volume = 100;
    private InputStream is;
    ResourceProvider resourceProvider;
    private final boolean useStringUrls;

    public AudioPlayer(ResourceProvider resourceProvider, int volume, boolean useStringUrls) {
        this.volume = volume >= 0 ? volume : 100;
        this.resourceProvider = resourceProvider;
        this.useStringUrls = useStringUrls;
    }

    public synchronized void playAudio(String resourcePath, PlayerListener playerListener, boolean prefetch)
            throws CheckedException {
        if (player != null) {
            stopAndDeallocate();
        }

        this.playerListener = playerListener;
        String url = resourceProvider.getAudioResourceUrl(resourcePath);

        if (useStringUrls && url != null && url.toLowerCase().indexOf("file") == 0) {
            playUrl(url, playerListener, prefetch);
        } else {
            // playStream(resourcePath, playerListener, prefetch);
            playStream(url, playerListener, prefetch);
        }

    }

    protected String getAudioMimeType(String audioResourcePath) {
        String audioFileType = audioResourcePath.substring(audioResourcePath.lastIndexOf('.') + 1);
        if ("mp3".equals(audioFileType)) {
            return Constants.MIME_TYPE_MPEG;
        } else if ("wav".equals(audioFileType)) {
            return "audio/x-wav";
        }
        throw new IllegalArgumentException("audioResourcePath must end in a valid file extension. resource: "
                + audioResourcePath);
    }

    protected void playStream(String resourcePath, PlayerListener playerListener, boolean prefetch)
            throws CheckedException {

        /*
         * TODO: we should do exception translation here and throw a checked business exception
         * after we log the true exception type
         */

        try {
            is = getInputStream(resourcePath);
            if (is == null) {
                Log.debug("Null InputStream for path: " + resourcePath);
            } else {
                /*
                 * NOTE: we only get the resource URL to get the fully-qualified path in case the
                 * file extension is not defined in the project index. We need this to get the file
                 * extension to determine the mimetype.
                 */
                player = createPlayer(is, getAudioMimeType(resourcePath));
                if (playerListener != null) {
                    player.addPlayerListener(playerListener);
                }
                this.play(prefetch);
            }
        } catch (MediaException me) {
            handleMediaException(me);
        } catch (IOException ioe_ignore) {
            // In MicroEmulator, a java.io.FileNotFoundException is thrown, but that class is not
            // included in j2me. So we cannot actually test for it. Just log the error and ignore
            // it.
            Log.warn("IOException: [" + ioe_ignore.getClass().getName() + "] playing resource: " + resourcePath,
                    ioe_ignore);
        }
    }

    protected Player createPlayer(final InputStream in, final String mimeType) throws MediaException, IOException {
        return Manager.createPlayer(is, mimeType);
    }

    protected Player createPlayer(String url) throws MediaException, IOException {
        return Manager.createPlayer(url);
    }

    protected void playUrl(String url, PlayerListener playerListener, boolean prefetch) throws CheckedException {
        try {
            player = createPlayer(url);

            if (player == null) {
                Log.warn("Could not create player for url: " + url);
                return;
            }

            if (playerListener != null) {
                player.addPlayerListener(playerListener);
            }
            this.play(prefetch);
        } catch (MediaException me) {
            handleMediaException(me);
        } catch (IOException ioe_ignore) {
            Log.warn("IOException: [" + ioe_ignore.getClass().getName() + "] playing resource: " + url,
                    ioe_ignore);
        }
    }

    public synchronized void playAudio(String resourcePath, boolean prefetch) throws CheckedException {
        playAudio(resourcePath, null, prefetch);
    }

    /**
     * 
     * @param subpath
     * @return
     * @throws IOException
     */
    protected InputStream getInputStream(String subpath) throws IOException {
        return resourceProvider.loadAudio(subpath);
    }

    protected void handleMediaException(MediaException me) throws CheckedException {
        if (isPhoneInSilentMode(me)) {
            throw new CheckedException(me, CheckedException.PHONE_PROFILE_SILENT_MODE_ERROR);
        } else {
            Log.warn("MediaException: ", me);
        }
    }

    public synchronized void stop() {
        try {
            if (player != null && player.getState() == Player.STARTED)
                player.stop();
        } catch (MediaException me) {
            Log.warn("Could not stop audio player.", me);
        }
    }

    public synchronized void play(boolean prefetch) throws CheckedException {
        try {
            if (player.getState() == Player.UNREALIZED) {
                player.realize();
                VolumeControl vc = (VolumeControl) player.getControl("VolumeControl");

                if (vc != null) {
                    vc.setLevel(volume);
                    if (volume > 0 && vc.isMuted()) {
                        vc.setMute(false);
                    } else if (volume == 0 && !vc.isMuted()) {
                        vc.setMute(true);
                    }
                }
            }

            if (prefetch && (player.getState() == Player.REALIZED)) {
                player.prefetch();
            }
            player.start();
        } catch (MediaException me) {
            handleMediaException(me);
        }
    }

    /**
     * @param me
     *            the {@link MediaException} that was thrown when we tried to play audio
     * @return true if we are sure that the exception was thrown because the phone profile is set to
     *         silent mode, otherwise false.
     */
    protected boolean isPhoneInSilentMode(MediaException me) {
        String msg = me.getMessage();

        // nokia s40 (and others?) includes the string "sounds not allowed" when
        // a MediaException is thrown b/c the phone is in silent mode
        if (msg != null && msg.toLowerCase().indexOf("sounds not allowed") >= 0) {
            return true;
        }
        return false;
    }

    protected synchronized void stopAndDeallocate() {
        if (player != null) {
            if (player.getState() == Player.STARTED) {
                stop();
            }

            if (player.getState() != Player.CLOSED) {
                if (playerListener != null) {
                    player.removePlayerListener(playerListener);
                    playerListener = null;
                }
                player.deallocate();
                player.close();
                // player = null;
            }
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    // do nothing
                }
            }
            // Ugly, but we may need to force GC to help reclaim resources
            // System.gc();
        }
    }

    public synchronized void cleanup() {
        stopAndDeallocate();
        player = null;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
