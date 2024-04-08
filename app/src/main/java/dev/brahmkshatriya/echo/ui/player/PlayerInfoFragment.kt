package dev.brahmkshatriya.echo.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import dev.brahmkshatriya.echo.databinding.FragmentPlayerInfoBinding
import dev.brahmkshatriya.echo.utils.autoCleared
import dev.brahmkshatriya.echo.utils.emit
import dev.brahmkshatriya.echo.utils.observe
import dev.brahmkshatriya.echo.viewmodels.UiViewModel

class PlayerInfoFragment : Fragment() {
    var binding by autoCleared<FragmentPlayerInfoBinding>()
    private val uiViewModel by activityViewModels<UiViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observe(uiViewModel.systemInsets) {
            binding.viewCard.updateLayoutParams<MarginLayoutParams> {
                topMargin = it.top
            }
        }

        observe(uiViewModel.infoSheetOffset) {
            binding.buttonToggleGroup.translationY = it * uiViewModel.systemInsets.value.top
            binding.viewCard.alpha = it
        }

        binding.viewPager.getChildAt(0).run {
            this as RecyclerView
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = PlayerInfoAdapter(this)

        binding.buttonToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) {
                emit(uiViewModel.changeInfoState) { STATE_COLLAPSED }
                return@addOnButtonCheckedListener
            }
            emit(uiViewModel.changeInfoState) { STATE_EXPANDED }
            val index = when (checkedId) {
                binding.upNext.id -> 0
                binding.lyrics.id -> 1
                binding.info.id -> 2
                else -> throw IllegalArgumentException("Invalid checkedId")
            }
            binding.viewPager.setCurrentItem(index, false)
        }
    }

    class PlayerInfoAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount() = 3

        override fun createFragment(position: Int) = when (position) {
            0 -> PlaylistFragment()
            1 -> LyricsFragment()
            2 -> TrackDetailsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }

    }
}