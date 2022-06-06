data class Response(
	val daftarOrder: List<DaftarOrderItem?>? = null
)

data class DaftarOrderItem(
	val jenisHandphone: String? = null,
	val biaya: Int? = null,
	val nama: String? = null,
	val nomorOrder: String? = null,
	val waktuOrder: Int? = null,
	val nomorHp: String? = null,
	val status: String? = null
)

