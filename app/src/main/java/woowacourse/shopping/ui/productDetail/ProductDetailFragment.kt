package woowacourse.shopping.ui.productDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.ui.util.UniversalViewModelFactory
import woowacourse.shopping.databinding.FragmentProductDetailBinding
import woowacourse.shopping.ui.FragmentNavigator

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("FragmentCartListBinding is not initialized")

    private lateinit var factory: UniversalViewModelFactory
    private lateinit var viewModel: ProductDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater)

        initViewModel()

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.onItemChargeListener = viewModel
        binding.onProductClickListener = viewModel

        return binding.root
    }

    private fun initViewModel() {
        arguments?.let {
            factory = ProductDetailViewModel.factory(productId = it.getLong(PRODUCT_ID))
        }
        viewModel = ViewModelProvider(this, factory)[ProductDetailViewModel::class.java]
    }

    private fun showSkeletonUi() {
        binding.includeProductDetailShimmer.root.visibility = View.VISIBLE
        binding.layoutProductDetail.visibility = View.GONE
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        showSkeletonUi()
        lifecycleScope.launch {
            delay(1000)
            viewModel.loadAll()
        }
        observeCurrentProduct()
        viewModel.detailProductDestinationId.observe(viewLifecycleOwner) {
            navigateToProductDetail(it)
        }
        binding.productDetailToolbar.setOnMenuItemClickListener {
            navigateToMenuItem(it)
        }
    }

    private fun observeCurrentProduct() {
        viewModel.currentProduct.observe(viewLifecycleOwner) {
            binding.includeProductDetailShimmer.root.stopShimmer()
            binding.includeProductDetailShimmer.root.visibility = View.GONE
            binding.layoutProductDetail.visibility = View.VISIBLE
        }
    }

    private fun navigateToProductDetail(id: Long) =
        (requireActivity() as? FragmentNavigator)?.navigateToProductDetail(id)

    private fun navigateToMenuItem(it: MenuItem) =
        when (it.itemId) {
            R.id.action_x -> {
                (requireActivity() as? FragmentNavigator)?.popBackStack()
                true
            }

            else -> false
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PRODUCT_ID = "productId"
        const val TAG = "ProductDetailFragment"

    }
}
