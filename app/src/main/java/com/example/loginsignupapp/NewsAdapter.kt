import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupapp.NewsItem
import com.example.loginsignupapp.R
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var newsList = emptyList<NewsItem>()

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.news_title)
        val summaryTextView: TextView = itemView.findViewById(R.id.news_summary)
        val dateTextView: TextView = itemView.findViewById(R.id.news_date)
        val readMoreButton: Button = itemView.findViewById(R.id.read_more_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val layoutId = if (viewType == 1) R.layout.featured_news_card else R.layout.news_card
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return NewsViewHolder(view)
    }

    // Add this to your NewsAdapter class
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]

        holder.titleTextView.text = newsItem.title
        holder.summaryTextView.text = newsItem.summary ?: "No summary available"
        holder.dateTextView.text = formatDate(newsItem.time)

        holder.readMoreButton.setOnClickListener {
            // Open news URL in browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.url))
            holder.itemView.context.startActivity(intent)
        }

        // Add entrance animation
        holder.itemView.animate()
            .alpha(0f)
            .setDuration(0)
            .withStartAction {
                holder.itemView.alpha = 0f
            }
            .withEndAction {
                holder.itemView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
            .start()
    }
    override fun getItemCount(): Int = newsList.size

    override fun getItemViewType(position: Int): Int {
        return if (newsList[position].isFeatured) 1 else 0
    }

    fun updateNews(newNews: List<NewsItem>) {
        newsList = newNews
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}