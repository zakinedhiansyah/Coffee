/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utdi.zakinedhiansyah.coffee.data

data class OrderUiState(
    /** Memilih berapa kopi yang mau dipesan (1, 6, 12) */
    val quantity: Int = 0,
    /** Memilih rasa yang mau dipesan */
    val flavor: String = "",
    /** tanggal */
    val date: String = "",
    /** Total  harga yang dipesan */
    val price: String = "",
    /** Tanggal PickUp*/
    val pickupOptions: List<String> = listOf()
)
