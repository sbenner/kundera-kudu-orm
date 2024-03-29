/**
 * Copyright 2012 Impetus Infotech.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.impetus.kundera.entity.photographer;

import com.impetus.kundera.entity.album.AlbumUni_1_1_1_1;

import javax.persistence.*;

/**
 * Entity class representing a photographer
 *
 * @author amresh.singh
 */

@Entity
@Table(name = "PHOTOGRAPHER", schema = "KunderaTest@kunderatest")
public class PhotographerUni_1_1_1_1 {
    @Id
    @Column(name = "PHOTOGRAPHER_ID")
    private int photographerId;

    @Column(name = "PHOTOGRAPHER_NAME")
    private String photographerName;

    // One to many, will be persisted separately
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ALBUM_ID")
    private AlbumUni_1_1_1_1 album;

    /**
     * @return the photographerName
     */
    public String getPhotographerName() {
        return photographerName;
    }

    /**
     * @param photographerName
     *            the photographerName to set
     */
    public void setPhotographerName(String photographerName) {
        this.photographerName = photographerName;
    }

    /**
     * @return the photographerId
     */
    public int getPhotographerId() {
        return photographerId;
    }

    /**
     * @param photographerId
     *            the photographerId to set
     */
    public void setPhotographerId(int photographerId) {
        this.photographerId = photographerId;
    }

    /**
     * @return the album
     */
    public AlbumUni_1_1_1_1 getAlbum() {
        return album;
    }

    /**
     * @param album
     *            the album to set
     */
    public void setAlbum(AlbumUni_1_1_1_1 album) {
        this.album = album;
    }

}
