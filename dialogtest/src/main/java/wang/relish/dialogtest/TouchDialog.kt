package wang.relish.dialogtest

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author wangxin
 * @since 20200803
 */
class TouchDialog(
        context: Context
) : Dialog(
        context,
        R.style.LTEmojiDialogStyle
) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_listene_together_emoji)
        setCanceledOnTouchOutside(true)
        initViews()
    }

    private var mAdapter: LTEmojiAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    @SuppressLint("ClickableViewAccessibility")
    fun initViews() {
        val screenWidth = DimensionUtils.getScreenWidth(context)
        val dp30 = DimensionUtils.dpToPx(30F)
        val dp35 = DimensionUtils.dpToPx(35F) // 左右边距
        val emojiSize = DimensionUtils.dpToPx(50F) // Emoji宽度/高度
        val emojiGap =
                (screenWidth - (dp35 shl 1) - emojiSize * ROW_COUNT) / (ROW_COUNT - 1) // 每个Emoji记得间距
        val emojiRects = mutableListOf<Rect>()
        val rect = Rect()
        for (i in 0 until EMOJI_COUNT) {
            val left = dp35 + (i % ROW_COUNT) * (emojiSize + emojiGap)
            rect.left = left
            rect.right = left + emojiSize
            val top = (i / ROW_COUNT) * (emojiSize + dp30)
            rect.top = top
            rect.bottom = top + emojiSize
            emojiRects.add(rect)
        }
        mRecyclerView = findViewById(R.id.rvEmojis)
        mRecyclerView?.adapter = LTEmojiAdapter().also { mAdapter = it }
        mRecyclerView?.layoutManager = GridLayoutManager(context, ROW_COUNT)
        mRecyclerView?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position: Int = parent.getChildAdapterPosition(view)
                val isTop = position / ROW_COUNT == 0
                outRect.left = (position % ROW_COUNT) * emojiGap / ROW_COUNT
                outRect.right = emojiGap - (position % ROW_COUNT + 1) * emojiGap / ROW_COUNT
                outRect.top = if (isTop.not()) dp30 else 0
                outRect.bottom = 0
            }
        })
        mRecyclerView?.apply {
            setOnTouchListener { _, event ->
                val action = event.action
                Log.e("wangxin", when (action) {
                    MotionEvent.ACTION_UP -> "ACTION_UP"
                    MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
                    MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
                    MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
                    else -> "$action"
                })
//                when (action) {
//                    MotionEvent.ACTION_UP -> {
                        val x = event.x.toInt()
                        val y = event.y.toInt()
                        for (r in emojiRects) {
                            if (r.contains(x, y)) {
                                return@setOnTouchListener false
                            }
                        }
                        dismiss()
                        return@setOnTouchListener true
//                    }
//                    else -> return@setOnTouchListener false
//                }
            }
        }
    }

    companion object {
        private const val ROW_COUNT = 4
        private const val EMOJI_COUNT = 8
    }
}

class LTEmojiAdapter(
        private val mData: List<String> = arrayListOf("1", "2", "3", "4", "5", "6", "7", "8")
) : RecyclerView.Adapter<LTEmojiAdapter.LTEmojiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LTEmojiViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_listen_together_emoji, parent, false)
        return LTEmojiViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: LTEmojiViewHolder, position: Int) {
        val emoji = mData[position]
        holder.mEmoji.text = emoji
    }

    private fun buildEmojiClickAnimator(v: View): AnimatorSet {
        val scaleXSmall = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.85f)
        val scaleXNormal = ObjectAnimator.ofFloat(v, "scaleX", 0.85f, 1f)
        val scaleYSmall = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.85f)
        val scaleYNormal = ObjectAnimator.ofFloat(v, "scaleY", 0.85f, 1f)
        scaleXSmall.duration = 100
        scaleYSmall.duration = 100
        scaleXNormal.duration = 100
        scaleYNormal.duration = 100
        return AnimatorSet().apply {
            play(scaleXSmall).with(scaleYSmall).before(scaleXNormal).before(scaleYNormal)
        }
    }

    class LTEmojiViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val mEmoji: TextView = v.findViewById(R.id.ivEmoji)
    }
}
