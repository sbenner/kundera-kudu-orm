/*******************************************************************************
 * * Copyright 2012 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.kundera.entity.photographer;

import com.impetus.kundera.entity.album.AlbumBi_M_M_1_1;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a photographer
 *
 * @author amresh.singh
 */

@Entity
@Table(name = "PHOTOGRAPHER", schema = "KunderaTest@kunderatest")
public class PhotographerBi_M_M_1_1 {
    @Id
    @Column(name = "PHOTOGRAPHER_ID")
    private int photographerId;

    @Column(name = "PHOTOGRAPHER_NAME")
    private String photographerName;

    @ManyToMany
    @JoinTable(name = "PHOTOGRAPHER_ALBUM", joinColumns = {@JoinColumn(name = "PHOTOGRAPHER_ID")}, inverseJoinColumns = {@JoinColumn(name = "ALBUM_ID")})
    private List<AlbumBi_M_M_1_1> albums;

    /**
     * @return the photographerId
     */
    public int getPhotographerId() {
        return photographerId;
    }

    /**
     * @param photographerId the photographerId to set
     */
    public void setPhotographerId(int photographerId) {
        this.photographerId = photographerId;
    }

    /**
     * @return the photographerName
     */
    public String getPhotographerName() {
        return photographerName;
    }

    /**
     * @param photographerName the photographerName to set
     */
    public void setPhotographerName(String photographerName) {
        this.photographerName = photographerName;
    }

    /**
     * @return the albums
     */
    public List<AlbumBi_M_M_1_1> getAlbums() {
        return albums;
    }

    /**
     * @param albums the albums to set
     */
    public void addAlbum(AlbumBi_M_M_1_1 album) {
        if (this.albums == null || this.albums.isEmpty()) {
            this.albums = new ArrayList<AlbumBi_M_M_1_1>();
        }
        this.albums.add(album);
    }

}
