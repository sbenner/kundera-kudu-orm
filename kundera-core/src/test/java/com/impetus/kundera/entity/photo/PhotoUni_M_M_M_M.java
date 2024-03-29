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
package com.impetus.kundera.entity.photo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class for photo
 *
 * @author amresh.singh
 */

@Entity
@Table(name = "PHOTO", schema = "KunderaTest@kunderatest")
public class PhotoUni_M_M_M_M {
    @Id
    @Column(name = "PHOTO_ID")
    private String photoId;

    @Column(name = "PHOTO_CAPTION")
    private String photoCaption;

    @Column(name = "PHOTO_DESC")
    private String photoDescription;

    public PhotoUni_M_M_M_M() {

    }

    public PhotoUni_M_M_M_M(String photoId, String caption, String description) {
        this.photoId = photoId;
        this.photoCaption = caption;
        this.photoDescription = description;
    }

    /**
     * @return the photoId
     */
    public String getPhotoId() {
        return photoId;
    }

    /**
     * @param photoId
     *            the photoId to set
     */
    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    /**
     * @return the photoCaption
     */
    public String getPhotoCaption() {
        return photoCaption;
    }

    /**
     * @param photoCaption
     *            the photoCaption to set
     */
    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }

    /**
     * @return the photoDescription
     */
    public String getPhotoDescription() {
        return photoDescription;
    }

    /**
     * @param photoDescription
     *            the photoDescription to set
     */
    public void setPhotoDescription(String photoDescription) {
        this.photoDescription = photoDescription;
    }

}
