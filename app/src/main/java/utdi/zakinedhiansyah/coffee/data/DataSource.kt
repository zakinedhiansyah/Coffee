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

import utdi.zakinedhiansyah.coffee.R
/** Data untuk menambahkan macam-macam rasa **/
object DataSource {
    val flavors = listOf(
        R.string.espesso,
        R.string.americano,
        R.string.frappe,
        R.string.cappucino
    )
    /** Untuk menentukan berapa kopi yang ingin dipesan**/
    val quantityOptions = listOf(
        Pair(R.string.one_coffee, 1),
        Pair(R.string.six_coffees, 6),
        Pair(R.string.twelve_coffees, 12)
    )
}
