import { Metadata } from 'next';
import Image from 'next/image';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { Star, ChevronRight } from 'lucide-react';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import ImageGallery from '@/components/catalog/ImageGallery';
import ProductActions from '@/components/catalog/ProductActions';
import FrequentlyBoughtTogether from '@/components/catalog/FrequentlyBoughtTogether';
import { Product } from '@/types';

interface ProductPageProps {
  params: Promise<{
    slug: string;
  }>;
}

// Mock data fetching function
async function getProduct(slug: string): Promise<Product | null> {
  // In a real app, this would be a fetch to your API
  if (slug === 'aerosound-elite-5') {
    return {
      id: '1',
      name: 'AeroSound Elite 5 - Premium Noise-Cancelling Headphones',
      slug: 'aerosound-elite-5',
      description: 'The AeroSound Elite 5 redefines acoustic excellence. Equipped with our proprietary ANC 4.0 chip, these headphones analyze ambient noise 50,000 times per second to create an immersive bubble of silence, no matter where you are.',
      basePrice: 350.00,
      discountPrice: 299.00,
      discountPercentage: 15,
      sku: 'AS-ELITE5-BLK',
      rating: 4.5,
      reviewsCount: 120,
      active: true,
      imageUrl: '/images/products/aerosound-elite-5.svg',
      images: [
        '/images/products/aerosound-elite-5.svg',
        '/images/products/aerosound-elite-5-2.svg',
        '/images/products/aerosound-elite-5-3.svg',
        '/images/products/aerosound-elite-5-4.svg'
      ],
      colors: [
        { name: 'Matte Black', hex: '#1A1A1A' },
        { name: 'Deep Sea', hex: '#1B2A4E' },
        { name: 'Silver Mist', hex: '#D1D5DB' }
      ],
      categoryName: 'Audio'
    };
  }
  return null;
}

export async function generateMetadata({ params }: ProductPageProps): Promise<Metadata> {
  const { slug } = await params;
  const product = await getProduct(slug);
  
  if (!product) return { title: 'Product Not Found' };
  
  return {
    title: `${product.name} | Bazario Store`,
    description: product.description,
  };
}

export default async function Page({ params }: ProductPageProps) {
  const { slug } = await params;
  const product = await getProduct(slug);

  if (!product) {
    notFound();
  }

  const suggestedItems = [
    {
      id: 'acc-1',
      name: 'Premium Aluminum Headphone Stand',
      price: 45.00,
      imageUrl: '/images/products/headphone-stand.svg'
    },
    {
      id: 'acc-2',
      name: 'Hardshell Travel Protection Case',
      price: 29.00,
      imageUrl: '/images/products/travel-case.svg'
    }
  ];

  return (
    <div className="min-h-screen bg-surface">
      <Header />
      
      <main className="max-w-container-max mx-auto px-margin-desktop py-stack-md">
        {/* Breadcrumbs */}
        <nav className="mb-stack-md">
          <ol className="flex items-center gap-2 text-label-md font-label-md text-on-surface-variant">
            <li><Link href="/" className="hover:text-primary">Home</Link></li>
            <li className="flex items-center gap-2">
              <ChevronRight className="w-4 h-4" /> 
              <Link href="/category/electronics" className="hover:text-primary">Electronics</Link>
            </li>
            <li className="flex items-center gap-2">
              <ChevronRight className="w-4 h-4" /> 
              <Link href="/category/audio" className="hover:text-primary">Audio</Link>
            </li>
            <li className="flex items-center gap-2 text-on-surface">
              <ChevronRight className="w-4 h-4" /> 
              {product.name.split(' - ')[0]}
            </li>
          </ol>
        </nav>

        {/* Product Core */}
        <section className="grid grid-cols-1 lg:grid-cols-2 gap-gutter items-start">
          <ImageGallery images={product.images || [product.imageUrl]} />
          
          <div className="flex flex-col gap-6 lg:pl-gutter">
            <div>
              <h1 className="font-headline-lg text-headline-lg text-on-surface mb-2">
                {product.name}
              </h1>
              <div className="flex items-center gap-4">
                <div className="flex items-center gap-1 text-tertiary">
                  {[...Array(4)].map((_, i) => (
                    <Star key={i} className="w-4 h-4 fill-current" />
                  ))}
                  <Star className="w-4 h-4" />
                  <span className="text-label-md font-label-md text-on-surface-variant ml-1">
                    ({product.reviewsCount} reviews)
                  </span>
                </div>
                <span className="px-2 py-0.5 bg-secondary-container text-on-secondary-container text-label-md font-label-md rounded-full">
                  In Stock
                </span>
              </div>
            </div>

            <div className="flex items-center gap-4 py-4 border-y border-outline-variant">
              <span className="text-price-lg font-price-lg text-on-surface">
                ${product.discountPrice?.toFixed(2) || product.basePrice.toFixed(2)}
              </span>
              {product.discountPrice && (
                <>
                  <span className="text-body-md font-body-md text-on-surface-variant line-through">
                    ${product.basePrice.toFixed(2)}
                  </span>
                  <span className="px-2 py-1 bg-error-container text-on-error-container text-label-md font-label-md rounded-lg">
                    -{product.discountPercentage}% OFF
                  </span>
                </>
              )}
            </div>

            <ProductActions colors={product.colors} />
          </div>
        </section>

        {/* Description Section */}
        <section className="mt-stack-lg border-t border-outline-variant">
          <div className="flex border-b border-outline-variant overflow-x-auto">
            <button className="px-8 py-5 text-label-md font-label-md border-b-2 border-primary text-primary uppercase tracking-widest whitespace-nowrap">
              Description
            </button>
            <button className="px-8 py-5 text-label-md font-label-md text-on-surface-variant hover:text-primary transition-colors uppercase tracking-widest whitespace-nowrap">
              Specifications
            </button>
            <button className="px-8 py-5 text-label-md font-label-md text-on-surface-variant hover:text-primary transition-colors uppercase tracking-widest whitespace-nowrap">
              Reviews ({product.reviewsCount})
            </button>
          </div>
          
          <div className="py-stack-md">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-stack-lg items-center">
              <div className="flex flex-col gap-4">
                <h3 className="text-headline-md font-headline-md">Experience Pure Silence.</h3>
                <p className="text-body-lg text-on-surface-variant leading-relaxed">
                  {product.description}
                </p>
                <p className="text-body-md text-on-surface-variant">
                  From the bustling city streets to the hum of a jet engine, focus only on what matters: your sound. With up to 60 hours of battery life and studio-grade high-fidelity drivers, the Elite 5 isn&apos;t just a gadget—it&apos;s an investment in your auditory lifestyle.
                </p>
              </div>
              <div className="rounded-2xl overflow-hidden shadow-xl border border-outline-variant relative h-[400px]">
                <Image
                  src="/images/products/aerosound-lifestyle.svg"
                  alt="Lifestyle image"
                  fill
                  className="object-cover"
                  sizes="(max-width: 768px) 100vw, 50vw"
                />
              </div>
            </div>
          </div>
        </section>

        <FrequentlyBoughtTogether 
          currentProduct={{
            id: product.id,
            name: product.name.split(' - ')[0],
            price: product.discountPrice || product.basePrice,
            imageUrl: product.imageUrl
          }}
          suggestedItems={suggestedItems}
        />
      </main>

      <Footer />
    </div>
  );
}
